package org.examora.examora.notes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.examora.examora.notes.entities.Note;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaisieNotesDTO {
    private Long   noteId;
    private Long   etudiantId;
    private Long   matiereId;
    private Long   semestreId;
    private String anneeUniversitaire;

    private String matricule;
    private String nomComplet;
    private String  matiereNom;
    private boolean hasTp;

    private Double noteDS;
    private Double noteTP;
    private Double noteExamen;
    private Double  noteFusionnee;
    private boolean valideeProfesseur;
    private boolean admissible;
    private boolean noteComplete;
    private LocalDateTime dateModification;
    public static SaisieNotesDTO fromNote(Note note) {
        String nomComplet = note.getEtudiant().getPrenom()
                + " " + note.getEtudiant().getNom();

        return SaisieNotesDTO.builder()
                .noteId(note.getId())
                .etudiantId(note.getEtudiant().getId())
                .matiereId(note.getMatiere().getId())
                .semestreId(note.getSemestre().getId())
                .anneeUniversitaire(note.getAnneeUniversitaire())
                .matricule(note.getEtudiant().getMatricule())
                .nomComplet(nomComplet)
                .matiereNom(note.getMatiere().getNom())
                .hasTp(note.isHasTp())
                .noteDS(note.getNoteDS())
                .noteTP(note.getNoteTP())
                .noteExamen(note.getNoteExamen())
                .noteFusionnee(note.getNoteFusionnee())
                .valideeProfesseur(note.isValideeProfesseur())
                .admissible(note.estAdmissible())
                .noteComplete(note.estComplete())
                .dateModification(note.getDateModification())
                .build();
    }

    public String getFormuleAppliquee() {
        return hasTp
                ? "25% DS + 25% TP + 50% Examen"
                : "35% DS + 65% Examen";
    }
}
