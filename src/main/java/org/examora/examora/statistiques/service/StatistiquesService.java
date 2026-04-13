package org.examora.examora.statistiques.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.academique.entities.UE;
import org.examora.examora.academique.repository.MatiereRepository;
import org.examora.examora.notes.entities.Note;
import org.examora.examora.notes.repository.NoteRepository;
import org.examora.examora.notes.repository.SemestreRepository;
import org.examora.examora.statistiques.dto.*;
import org.examora.examora.statistiques.entities.ProgressionEtudiant;
import org.examora.examora.statistiques.entities.StatistiquesExamen;
import org.examora.examora.statistiques.repository.AlertePrecoceRepository;
import org.examora.examora.statistiques.repository.ProgressionEtudiantRepository;
import org.examora.examora.statistiques.repository.StatistiquesExamenRepository;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.examora.examora.utilisateur.repository.EtudiantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatistiquesService {

    private final NoteRepository                  noteRepository;
    private final MatiereRepository               matiereRepository;
    private final SemestreRepository              semestreRepository;
    private final EtudiantRepository              etudiantRepository;
    private final StatistiquesExamenRepository    statsExamenRepo;
    private final ProgressionEtudiantRepository   progressionRepo;
    private final AlertePrecoceRepository         alerteRepo;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. CALCUL ET SAUVEGARDE DES STATISTIQUES D'UNE MATIÈRE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calcule (ou recalcule) les statistiques d'une matière pour un semestre/année.
     * Appelé automatiquement après validation des notes.
     */
    @Transactional
    public StatistiquesExamenDTO calculerEtSauvegarder(Long matiereId, Long semestreId,
                                                       String anneeUniversitaire) {
        Matiere  matiere  = chargerMatiere(matiereId);
        Semestre semestre = chargerSemestre(semestreId);

        List<Note> notes = noteRepository
                .findByMatiereAndSemestreAndAnneeUniversitaireAndValideeProfesseurTrue(
                        matiere, semestre, anneeUniversitaire);

        List<Double> valeurs = notes.stream()
                .map(Note::getNoteFusionnee)
                .filter(Objects::nonNull)
                .toList();

        StatistiquesExamen stats = statsExamenRepo
                .findByMatiereAndSemestreAndAnneeUniversitaire(matiere, semestre, anneeUniversitaire)
                .orElseGet(() -> StatistiquesExamen.builder()
                        .matiere(matiere)
                        .semestre(semestre)
                        .anneeUniversitaire(anneeUniversitaire)
                        .build());

        stats.setNbInscrits(notes.size());
        stats.setNbNotesValidees(valeurs.size());

        if (!valeurs.isEmpty()) {
            double moyenne = valeurs.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double min     = valeurs.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max     = valeurs.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double ecart   = calculerEcartType(valeurs, moyenne);
            long   admis   = valeurs.stream().filter(n -> n >= 10.0).count();

            stats.setMoyenneClasse(arrondir(moyenne));
            stats.setNoteMin(arrondir(min));
            stats.setNoteMax(arrondir(max));
            stats.setEcartType(arrondir(ecart));
            stats.setTauxReussite(arrondir((double) admis / valeurs.size()));

            // Distribution par tranches
            stats.setNbMoins5 ((int) valeurs.stream().filter(n -> n < 5).count());
            stats.setNb5a10   ((int) valeurs.stream().filter(n -> n >= 5  && n < 10).count());
            stats.setNb10a14  ((int) valeurs.stream().filter(n -> n >= 10 && n < 14).count());
            stats.setNb14a17  ((int) valeurs.stream().filter(n -> n >= 14 && n < 17).count());
            stats.setNb17Plus ((int) valeurs.stream().filter(n -> n >= 17).count());
        }

        StatistiquesExamen saved = statsExamenRepo.save(stats);
        log.info("[STATS] Calculé — matière:{} semestre:{} tauxReussite:{}",
                matiere.getNom(), semestre.getNom(), saved.getTauxReussite());

        return StatistiquesExamenDTO.fromEntity(saved);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. PROGRESSION D'UN ÉTUDIANT
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calcule et persiste la progression d'un étudiant pour un semestre.
     * Nécessite que les notes du semestre soient validées.
     */
    @Transactional
    public ProgressionEtudiantDTO calculerProgression(Long etudiantId, Long semestreId,
                                                      String anneeUniversitaire) {
        Etudiant etudiant = chargerEtudiant(etudiantId);
        Semestre semestre = chargerSemestre(semestreId);

        List<Note> notes = noteRepository.findByEtudiantAndSemestreAndAnneeUniversitaire(
                etudiant, semestre, anneeUniversitaire);

        // Moyenne pondérée par crédits de la matière
        double sommePonderee = 0;
        double totalCredits  = 0;
        int    nbEchouees    = 0;
        int    creditsValides = 0;

        for (Note note : notes) {
            if (note.getNoteFusionnee() == null) continue;
            int credits = note.getMatiere().getCredits();
            sommePonderee += note.getNoteFusionnee() * credits;
            totalCredits  += credits;
            if (note.getNoteFusionnee() < 10.0) nbEchouees++;
            else creditsValides += credits;
        }

        Double moyenneSemestre = totalCredits > 0
                ? arrondir(sommePonderee / totalCredits)
                : null;

        // Delta par rapport au semestre précédent
        Double delta = calculerDelta(etudiant, semestre, moyenneSemestre);

        // Rang dans la filière (calculé sur toutes les progressions déjà sauvegardées)
        int[] rangEffectif = calculerRang(etudiant, semestre, anneeUniversitaire, moyenneSemestre);

        ProgressionEtudiant progression = progressionRepo
                .findByEtudiantAndSemestreAndAnneeUniversitaire(etudiant, semestre, anneeUniversitaire)
                .orElseGet(() -> ProgressionEtudiant.builder()
                        .etudiant(etudiant)
                        .semestre(semestre)
                        .anneeUniversitaire(anneeUniversitaire)
                        .build());

        progression.setMoyenneSemestre(moyenneSemestre);
        progression.setCreditsValides(creditsValides);
        progression.setNbMatieresEchouees(nbEchouees);
        progression.setDeltaMoyenne(delta);
        progression.setRangFiliere(rangEffectif[0]);
        progression.setEffectifFiliere(rangEffectif[1]);

        ProgressionEtudiant saved = progressionRepo.save(progression);
        log.info("[STATS] Progression — étudiant:{} semestre:{} moyenne:{}",
                etudiant.getMatricule(), semestre.getNom(), moyenneSemestre);

        return ProgressionEtudiantDTO.fromEntity(saved);
    }

    /** Historique complet de progression d'un étudiant */
    public List<ProgressionEtudiantDTO> getHistoriqueEtudiant(Long etudiantId) {
        Etudiant etudiant = chargerEtudiant(etudiantId);
        return progressionRepo.findHistoriqueEtudiant(etudiant)
                .stream()
                .map(ProgressionEtudiantDTO::fromEntity)
                .toList();
    }

    /** Classement d'un semestre (triés par moyenne décroissante) */
    public List<ProgressionEtudiantDTO> getClassementSemestre(Long semestreId, String annee) {
        Semestre semestre = chargerSemestre(semestreId);
        return progressionRepo.findClassementSemestre(semestre, annee)
                .stream()
                .map(ProgressionEtudiantDTO::fromEntity)
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. TABLEAU DE BORD SEMESTRE (ADMIN)
    // ─────────────────────────────────────────────────────────────────────────

    public TableauBordSemestreDTO getTableauBordSemestre(Long semestreId, String annee) {
        Semestre semestre = chargerSemestre(semestreId);

        List<StatistiquesExamen> statsListe = statsExamenRepo
                .findBySemestreAndAnneeUniversitaire(semestre, annee);

        List<StatistiquesExamenDTO> statsDTOs = statsListe.stream()
                .map(StatistiquesExamenDTO::fromEntity)
                .toList();

        List<StatistiquesExamenDTO> critiques = statsListe.stream()
                .filter(s -> s.getTauxReussite() != null && s.getTauxReussite() < 0.5)
                .map(StatistiquesExamenDTO::fromEntity)
                .toList();

        List<AlertePrecoceDTO> alertes = alerteRepo
                .findAlertesActives(semestre, annee)
                .stream()
                .map(AlertePrecoceDTO::fromEntity)
                .toList();

        // Moyennes globales agrégées
        OptionalDouble moyGlobale = statsListe.stream()
                .filter(s -> s.getMoyenneClasse() != null)
                .mapToDouble(StatistiquesExamen::getMoyenneClasse)
                .average();

        OptionalDouble tauxGlobal = statsListe.stream()
                .filter(s -> s.getTauxReussite() != null)
                .mapToDouble(StatistiquesExamen::getTauxReussite)
                .average();

        int nbEtudiants = statsListe.stream()
                .mapToInt(StatistiquesExamen::getNbInscrits)
                .max()
                .orElse(0);

        return TableauBordSemestreDTO.builder()
                .semestreId(semestre.getId())
                .semestreNom(semestre.getNom())
                .anneeUniversitaire(annee)
                .nbEtudiants(nbEtudiants)
                .moyenneGenerale(moyGlobale.isPresent() ? arrondir(moyGlobale.getAsDouble()) : null)
                .tauxReussiteGlobal(tauxGlobal.isPresent() ? arrondir(tauxGlobal.getAsDouble()) : null)
                .nbAlertesActives(alertes.size())
                .statistiquesParMatiere(statsDTOs)
                .matieresCritiques(critiques)
                .alertesActives(alertes)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. STATS PAR MATIÈRE (PROF)
    // ─────────────────────────────────────────────────────────────────────────

    public StatistiquesExamenDTO getStatistiquesMatiere(Long matiereId, Long semestreId, String annee) {
        Matiere  matiere  = chargerMatiere(matiereId);
        Semestre semestre = chargerSemestre(semestreId);
        return statsExamenRepo
                .findByMatiereAndSemestreAndAnneeUniversitaire(matiere, semestre, annee)
                .map(StatistiquesExamenDTO::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Statistiques non encore calculées pour cette matière"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVÉS
    // ─────────────────────────────────────────────────────────────────────────

    private Double calculerDelta(Etudiant etudiant, Semestre semestre, Double moyenneActuelle) {
        if (moyenneActuelle == null || semestre.getNumero() <= 1) return null;
        // Chercher le semestre précédent (même filière, numéro - 1)
        return progressionRepo.findHistoriqueEtudiant(etudiant)
                .stream()
                .filter(p -> p.getSemestre().getFiliere().getId()
                        .equals(semestre.getFiliere().getId())
                        && p.getSemestre().getNumero() == semestre.getNumero() - 1)
                .findFirst()
                .map(p -> p.getMoyenneSemestre() != null
                        ? arrondir(moyenneActuelle - p.getMoyenneSemestre())
                        : null)
                .orElse(null);
    }

    private int[] calculerRang(Etudiant etudiant, Semestre semestre,
                               String annee, Double moyenneActuelle) {
        if (moyenneActuelle == null) return new int[]{0, 0};
        List<ProgressionEtudiant> classement = progressionRepo.findClassementSemestre(semestre, annee);
        int effectif = classement.size() + 1; // +1 pour inclure l'étudiant courant
        long meilleurs = classement.stream()
                .filter(p -> !p.getEtudiant().getId().equals(etudiant.getId())
                        && p.getMoyenneSemestre() != null
                        && p.getMoyenneSemestre() > moyenneActuelle)
                .count();
        return new int[]{(int) meilleurs + 1, effectif};
    }

    private double calculerEcartType(List<Double> valeurs, double moyenne) {
        double variance = valeurs.stream()
                .mapToDouble(v -> Math.pow(v - moyenne, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
    }

    private static double arrondir(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private Etudiant chargerEtudiant(Long id) {
        return etudiantRepository.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Étudiant introuvable : id=" + id));
    }

    private Matiere chargerMatiere(Long id) {
        return matiereRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Matière introuvable : id=" + id));
    }

    private Semestre chargerSemestre(Long id) {
        return semestreRepository.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Semestre introuvable : id=" + id));
    }
}