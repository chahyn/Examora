package org.examora.examora.notes.service;

import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.notes.dto.ReleveNotesDTO;
import org.examora.examora.notes.entities.DecisionEtudiant;
import org.examora.examora.notes.entities.Note;
import org.examora.examora.notes.repository.DecisionEtudiantRepository;
import org.examora.examora.notes.repository.NoteRepository;
import org.examora.examora.notes.repository.SemestreRepository;
import org.examora.examora.notes.repository.UENotesQueryRepository;
import org.examora.examora.notes.repository.UENotesQueryRepository.LigneUEMatiere;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.examora.examora.utilisateur.repository.EtudiantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReleveNotesService {

    private final NoteRepository             noteRepository;
    private final DecisionEtudiantRepository decisionRepository;
    private final EtudiantRepository         etudiantRepository;
    private final SemestreRepository         semestreRepository;
    private final UENotesQueryRepository     ueNotesQueryRepository;

    public ReleveNotesDTO genererReleve(Long etudiantId, Long semestreId, String anneeUniversitaire) {

        Etudiant etudiant = etudiantRepository.findById(etudiantId.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Étudiant introuvable"));

        Semestre semestre = semestreRepository.findById(semestreId.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Semestre introuvable"));

        List<Note> notes = noteRepository
                .findByEtudiantAndSemestreAndAnneeUniversitaire(etudiant, semestre, anneeUniversitaire);

        Map<Long, Note> noteParMatiere = notes.stream()
                .collect(Collectors.toMap(n -> n.getMatiere().getId(), n -> n));

        List<LigneUEMatiere> lignes = ueNotesQueryRepository.findLignesUEParSemestre(semestre);

        Map<Long, List<LigneUEMatiere>> lignesParUE = lignes.stream()
                .collect(Collectors.groupingBy(LigneUEMatiere::ueId));

        List<ReleveNotesDTO.MatiereNoteDTO> lignesReleve = new ArrayList<>();
        double sommePonderee = 0;
        double totalCredits  = 0;
        int    creditsObtenu = 0;

        for (Map.Entry<Long, List<LigneUEMatiere>> entry : lignesParUE.entrySet()) {
            List<LigneUEMatiere> lignesUE = entry.getValue();
            int creditsUE = lignesUE.get(0).creditsUE();

            double moyUE = calculerMoyenneUE(lignesUE, noteParMatiere);
            sommePonderee += moyUE * creditsUE;
            totalCredits  += creditsUE;
            if (moyUE >= 10.0) creditsObtenu += creditsUE;

            for (LigneUEMatiere ligne : lignesUE) {
                Note note = noteParMatiere.get(ligne.matiereId());
                lignesReleve.add(ReleveNotesDTO.MatiereNoteDTO.builder()
                        .matiereNom(ligne.matiereNom())
                        .hasTp(ligne.hasTp())
                        .noteDS(note != null ? note.getNoteDS() : null)
                        .noteTP(note != null ? note.getNoteTP() : null)
                        .noteExamen(note != null ? note.getNoteExamen() : null)
                        .noteFusionnee(note != null ? note.getNoteFusionnee() : null)
                        .admissible(note != null && note.estAdmissible())
                        .coefficient(ligne.coefficient())
                        .nomUE(ligne.ueNom())
                        .creditsUE(ligne.creditsUE())
                        .build());
            }
        }

        double moyenneSemestre = totalCredits > 0
                ? Math.round((sommePonderee / totalCredits) * 100.0) / 100.0 : 0.0;

        Optional<DecisionEtudiant> decision = decisionRepository
                .findByEtudiantAndSemestreAndAnneeUniversitaire(etudiant, semestre, anneeUniversitaire);

        return ReleveNotesDTO.builder()
                .etudiantId(etudiant.getId())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .matricule(etudiant.getMatricule())
                .filiere(etudiant.getFiliere())
                .anneeEtude(etudiant.getAnneeEtude())
                .nomSemestre(semestre.getNom())
                .anneeUniversitaire(anneeUniversitaire)
                .matieres(lignesReleve)
                .moyenneSemestre(moyenneSemestre)
                .creditsObtenusSemestre(creditsObtenu)
                .creditsAnnuels(decision.map(DecisionEtudiant::getCreditsAnnuels).orElse(null))
                .moyenneAnnuelle(decision.map(DecisionEtudiant::getMoyenneAnnuelle).orElse(null))
                .decision(decision.map(d -> d.getDecision().name()).orElse(null))
                .deliberationFinalisee(decision.map(DecisionEtudiant::isDeliberationFinalisee).orElse(false))
                .build();
    }

    private double calculerMoyenneUE(List<LigneUEMatiere> lignesUE,
                                     Map<Long, Note> noteParMatiere) {
        double somme = 0;
        double total = 0;
        for (LigneUEMatiere ligne : lignesUE) {
            Note note = noteParMatiere.get(ligne.matiereId());
            if (note != null && note.getNoteFusionnee() != null) {
                somme += note.getNoteFusionnee() * ligne.coefficient();
                total += ligne.coefficient();
            }
        }
        return total > 0 ? somme / total : 0.0;
    }
}
