package org.examora.examora.notes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.notes.dto.DeliberationResultDTO;
import org.examora.examora.notes.entities.DecisionEtudiant;
import org.examora.examora.notes.entities.DecisionType;
import org.examora.examora.notes.entities.Note;
import org.examora.examora.notes.entities.RapportDeliberation;
import org.examora.examora.notes.repository.DecisionEtudiantRepository;
import org.examora.examora.notes.repository.EtudiantNotesQueryRepository;
import org.examora.examora.notes.repository.NoteRepository;
import org.examora.examora.notes.repository.RapportDeliberationRepository;
import org.examora.examora.notes.repository.SemestreRepository;
import org.examora.examora.notes.repository.UENotesQueryRepository;
import org.examora.examora.notes.repository.UENotesQueryRepository.LigneUEMatiere;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.examora.examora.utilisateur.entities.Utilisateur;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliberationService {

    private final NoteRepository noteRepository;
    private final DecisionEtudiantRepository  decisionRepository;
    private final RapportDeliberationRepository rapportRepository;
    private final SemestreRepository semestreRepository;
    private final UENotesQueryRepository ueNotesQueryRepository;
    private final EtudiantNotesQueryRepository etudiantNotesQueryRepository;

    private static final int    CREDITS_ADMISSION = 54;
    private static final int    CREDITS_TOTAL     = 60;
    private static final double MOYENNE_ADMISSION  = 10.0;
    private static final int    CREDITS_REDOUBLE  = 42;

    @Transactional
    public DeliberationResultDTO lancerDeliberation(Long semestreId,
                                                    String anneeUniversitaire,
                                                    Utilisateur admin) {
        Semestre semestre = chargerSemestre(semestreId);

        if (!noteRepository.sontToutesValidees(semestre, anneeUniversitaire)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Toutes les notes doivent être validées avant la délibération.");
        }

        List<LigneUEMatiere> lignes = ueNotesQueryRepository.findLignesUEParSemestre(semestre);
        if (lignes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Aucune UE/matière configurée pour ce semestre.");
        }

        Map<Long, List<LigneUEMatiere>> lignesParUE = lignes.stream()
                .collect(Collectors.groupingBy(LigneUEMatiere::ueId));

        List<Etudiant> etudiants = etudiantNotesQueryRepository
                .findEtudiantsAvecNotes(semestre, anneeUniversitaire);

        List<DecisionEtudiant> decisions = new ArrayList<>();
        for (Etudiant etudiant : etudiants) {
            decisions.add(delibererEtudiant(etudiant, semestre, anneeUniversitaire, lignesParUE));
        }

        List<DecisionEtudiant> saved = decisionRepository.saveAll(decisions);
        RapportDeliberation rapport = genererRapport(semestre, anneeUniversitaire, saved, admin);
        rapportRepository.save(rapport);

        log.info("[DELIBERATION] Semestre:{} {} — {} décisions. Taux:{}%",
                semestreId, anneeUniversitaire, saved.size(), rapport.getTauxReussite());

        return buildResultDTO(semestre, anneeUniversitaire, rapport, saved);
    }

    public DeliberationResultDTO getDeliberation(Long semestreId, String anneeUniversitaire) {
        Semestre semestre = chargerSemestre(semestreId);
        RapportDeliberation rapport = rapportRepository
                .findBySemestreAndAnneeUniversitaire(semestre, anneeUniversitaire)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aucune délibération trouvée pour ce semestre."));
        List<DecisionEtudiant> decisions = decisionRepository
                .findBySemestreAndAnneeUniversitaire(semestre, anneeUniversitaire);
        return buildResultDTO(semestre, anneeUniversitaire, rapport, decisions);
    }


    private DecisionEtudiant delibererEtudiant(Etudiant etudiant,
                                               Semestre semestre,
                                               String anneeUniversitaire,
                                               Map<Long, List<LigneUEMatiere>> lignesParUE) {

        List<Note> notes = noteRepository
                .findByEtudiantAndSemestreAndAnneeUniversitaire(
                        etudiant, semestre, anneeUniversitaire);

        Map<Long, Double> noteParMatiere = notes.stream()
                .filter(n -> n.getNoteFusionnee() != null)
                .collect(Collectors.toMap(
                        n -> n.getMatiere().getId(),
                        Note::getNoteFusionnee));

        double sommePonderee  = 0;
        double totalCredits   = 0;
        int    creditsSemestre = 0;

        for (Map.Entry<Long, List<LigneUEMatiere>> entry : lignesParUE.entrySet()) {
            List<LigneUEMatiere> lignesUE = entry.getValue();
            int creditsUE = lignesUE.get(0).creditsUE();

            double moyUE = calculerMoyenneUE(lignesUE, noteParMatiere);
            sommePonderee += moyUE * creditsUE;
            totalCredits  += creditsUE;
            if (moyUE >= MOYENNE_ADMISSION) creditsSemestre += creditsUE;
        }

        double moyenneSemestre = totalCredits > 0
                ? arrondir(sommePonderee / totalCredits) : 0.0;

        Optional<DecisionEtudiant> autreDecision = decisionRepository
                .findByEtudiantAndAnneeUniversitaire(etudiant, anneeUniversitaire)
                .stream()
                .filter(d -> !d.getSemestre().getId().equals(semestre.getId()))
                .findFirst();

        int    creditsAnnuels  = creditsSemestre
                + autreDecision.map(DecisionEtudiant::getCreditsSemestre).orElse(0);
        double moyenneAutre    = autreDecision.map(DecisionEtudiant::getMoyenneSemestre).orElse(0.0);
        double moyenneAnnuelle = autreDecision.isPresent()
                ? arrondir((moyenneSemestre + moyenneAutre) / 2.0)
                : moyenneSemestre;

        DecisionType decision = DecisionEtudiant.evaluerDecision(creditsAnnuels, moyenneAnnuelle);

        return DecisionEtudiant.builder()
                .etudiant(etudiant)
                .semestre(semestre)
                .anneeUniversitaire(anneeUniversitaire)
                .moyenneSemestre(moyenneSemestre)
                .creditsSemestre(creditsSemestre)
                .creditsAnnuels(creditsAnnuels)
                .moyenneAnnuelle(moyenneAnnuelle)
                .decision(decision)
                .commentaire(buildCommentaire(creditsAnnuels, moyenneAnnuelle, decision))
                .dateDeliberation(LocalDateTime.now())
                .deliberationFinalisee(true)
                .build();
    }

    private double calculerMoyenneUE(List<LigneUEMatiere> lignesUE,
                                     Map<Long, Double> noteParMatiere) {
        double somme = 0;
        double total = 0;
        for (LigneUEMatiere ligne : lignesUE) {
            Double note = noteParMatiere.get(ligne.matiereId());
            if (note != null) {
                somme += note * ligne.coefficient();
                total += ligne.coefficient();
            }
        }
        return total > 0 ? arrondir(somme / total) : 0.0;
    }


    private RapportDeliberation genererRapport(Semestre semestre,
                                               String anneeUniversitaire,
                                               List<DecisionEtudiant> decisions,
                                               Utilisateur generePar) {
        long nbAdmis    = decisions.stream().filter(d -> d.getDecision() == DecisionType.ADMIS).count();
        long nbAjournes = decisions.stream().filter(d -> d.getDecision() == DecisionType.AJOURNE).count();
        long nbRedouble = decisions.stream().filter(d -> d.getDecision() == DecisionType.REDOUBLE).count();
        double moyPromo = decisions.stream()
                .mapToDouble(d -> d.getMoyenneSemestre() != null ? d.getMoyenneSemestre() : 0.0)
                .average().orElse(0.0);

        return RapportDeliberation.builder()
                .semestre(semestre)
                .anneeUniversitaire(anneeUniversitaire)
                .dateDeliberation(LocalDateTime.now())
                .nbEtudiants(decisions.size())
                .nbAdmis((int) nbAdmis)
                .nbAjournes((int) nbAjournes)
                .nbRedoubles((int) nbRedouble)
                .moyennePromo(arrondir(moyPromo))
                .generePar(generePar)
                .build();
    }


    private String buildCommentaire(int credits, double moyenne, DecisionType decision) {
        return switch (decision) {
            case ADMIS    -> String.format("Admis(e) — %d/%d crédits — moyenne: %.2f",
                    credits, CREDITS_TOTAL, moyenne);
            case AJOURNE  -> String.format("Ajourné(e) — %d/%d crédits — moyenne: %.2f",
                    credits, CREDITS_TOTAL, moyenne);
            case REDOUBLE -> String.format("Redoublement — %d/%d crédits",
                    credits, CREDITS_TOTAL);
        };
    }

    private DeliberationResultDTO buildResultDTO(Semestre semestre,
                                                 String anneeUniversitaire,
                                                 RapportDeliberation rapport,
                                                 List<DecisionEtudiant> decisions) {
        return DeliberationResultDTO.builder()
                .semestreId(semestre.getId())
                .nomSemestre(semestre.getNom())
                .anneeUniversitaire(anneeUniversitaire)
                .dateDeliberation(rapport.getDateDeliberation())
                .nbEtudiants(rapport.getNbEtudiants())
                .nbAdmis(rapport.getNbAdmis())
                .nbAjournes(rapport.getNbAjournes())
                .nbRedoubles(rapport.getNbRedoubles())
                .tauxReussite(rapport.getTauxReussite() != null ? rapport.getTauxReussite() : 0.0)
                .moyennePromo(rapport.getMoyennePromo() != null ? rapport.getMoyennePromo() : 0.0)
                .decisions(decisions.stream()
                        .map(DeliberationResultDTO.EtudiantDecisionDTO::fromEntity)
                        .toList())
                .build();
    }

    private Semestre chargerSemestre(Long id) {
        return semestreRepository.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Semestre introuvable : id=" + id));
    }

    private static double arrondir(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
