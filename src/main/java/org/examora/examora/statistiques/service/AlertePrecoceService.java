package org.examora.examora.statistiques.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.academique.repository.SemestreRepository;
import org.examora.examora.notes.entities.Note;
import org.examora.examora.notes.repository.NoteRepository;
import org.examora.examora.statistiques.dto.AlertePrecoceDTO;
import org.examora.examora.statistiques.entities.AlertePrecoce;
import org.examora.examora.statistiques.entities.AlertePrecoce.NiveauRisque;
import org.examora.examora.statistiques.repository.AlertePrecoceRepository;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertePrecoceService {

    private static final double SEUIL_DS_INSUFFISANT = 8.0;
    private static final String ANNEE_EN_COURS        = "2025-2026"; // à externaliser en @Value

    private final NoteRepository        noteRepository;
    private final AlertePrecoceRepository alerteRepo;
    // NOTE : on injecte SemestreRepository de academique (pas celui de notes)
    private final org.examora.examora.academique.repository.SemestreRepository semestreRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // JOB PLANIFIÉ — Tous les lundis à 08h00
    // ─────────────────────────────────────────────────────────────────────────

    @Scheduled(cron = "0 0 8 * * MON")
    @Transactional
    public void verifierEtudiantsEnDifficulte() {
        log.info("[ALERTES] Démarrage vérification hebdomadaire des étudiants en difficulté");

        List<Semestre> semestresActifs = semestreRepository.findAll(); // filtre possible par année

        for (Semestre semestre : semestresActifs) {
            traiterSemestre(semestre, ANNEE_EN_COURS);
        }

        log.info("[ALERTES] Vérification terminée");
    }

    /**
     * Peut aussi être appelé manuellement via l'API admin.
     */
    @Transactional
    public int traiterSemestre(Semestre semestre, String anneeUniversitaire) {
        // Récupérer tous les étudiants avec noteDS insuffisante
        List<Etudiant> etudiantsRisque = noteRepository.findEtudiantsNotesDSInsuffisantes(
                semestre, anneeUniversitaire, SEUIL_DS_INSUFFISANT);

        if (etudiantsRisque.isEmpty()) {
            log.info("[ALERTES] Aucun étudiant en difficulté — semestre:{}", semestre.getNom());
            return 0;
        }

        // Regrouper par étudiant pour compter le nombre de matières à risque
        Map<Long, List<Etudiant>> parEtudiant = etudiantsRisque.stream()
                .collect(Collectors.groupingBy(e -> e.getId().longValue()));

        int nbAlertes = 0;

        for (Map.Entry<Long, List<Etudiant>> entry : parEtudiant.entrySet()) {
            Etudiant etudiant        = entry.getValue().get(0);
            int      nbMatieres      = entry.getValue().size();

            // Éviter les doublons si alerte déjà active cette semaine
            boolean dejaAlerte = alerteRepo
                    .existsByEtudiantAndSemestreAndAnneeUniversitaireAndTraiteeFalse(
                            etudiant, semestre, anneeUniversitaire);
            if (dejaAlerte) continue;

            // Calculer la moyenne DS réelle de cet étudiant
            Double moyenneDs = calculerMoyenneDS(etudiant, semestre, anneeUniversitaire);

            NiveauRisque niveau = determinerNiveau(nbMatieres, moyenneDs);
            String       message = construireMessage(etudiant, nbMatieres, moyenneDs);

            AlertePrecoce alerte = AlertePrecoce.builder()
                    .etudiant(etudiant)
                    .semestre(semestre)
                    .anneeUniversitaire(anneeUniversitaire)
                    .niveauRisque(niveau)
                    .message(message)
                    .nbMatieresRisque(nbMatieres)
                    .moyenneDsActuelle(moyenneDs)
                    .build();

            alerteRepo.save(alerte);
            nbAlertes++;

            log.warn("[ALERTES] {} — {} matières DS < {} — niveau:{}",
                    etudiant.getMatricule(), nbMatieres, SEUIL_DS_INSUFFISANT, niveau);
        }

        log.info("[ALERTES] {} alertes créées pour semestre:{}", nbAlertes, semestre.getNom());
        return nbAlertes;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LECTURE
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AlertePrecoceDTO> getAlertesActives(Long semestreId, String annee) {
        Semestre semestre = semestreRepository.findById(semestreId)
                .orElseThrow();
        return alerteRepo.findAlertesActives(semestre, annee)
                .stream()
                .map(AlertePrecoceDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AlertePrecoceDTO marquerTraitee(Long alerteId) {
        AlertePrecoce alerte = alerteRepo.findById(alerteId)
                .orElseThrow();
        alerte.setTraitee(true);
        return AlertePrecoceDTO.fromEntity(alerteRepo.save(alerte));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private Double calculerMoyenneDS(Etudiant etudiant, Semestre semestre, String annee) {
        List<Note> notes = noteRepository.findByEtudiantAndSemestreAndAnneeUniversitaire(
                etudiant, semestre, annee);
        return notes.stream()
                .map(Note::getNoteDS)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .stream()
                .boxed()
                .findFirst()
                .map(v -> Math.round(v * 100.0) / 100.0)
                .orElse(null);
    }

    private NiveauRisque determinerNiveau(int nbMatieres, Double moyenneDs) {
        if (nbMatieres >= 3 || (moyenneDs != null && moyenneDs < 6.0)) return NiveauRisque.ELEVE;
        if (nbMatieres == 2)                                            return NiveauRisque.MOYEN;
        return NiveauRisque.FAIBLE;
    }

    private String construireMessage(Etudiant etudiant, int nbMatieres, Double moyenneDs) {
        return String.format(
                "L'étudiant %s %s (classe %s) a %d matière(s) avec une note DS insuffisante. " +
                        "Moyenne DS actuelle : %.2f/20.",
                etudiant.getPrenom(), etudiant.getNom(),
                etudiant.getClasse(),
                nbMatieres,
                moyenneDs != null ? moyenneDs : 0.0);
    }
}