package org.examora.examora.notes.dto;

import lombok.Builder;
import lombok.Data;
import org.examora.examora.notes.entities.Note;

import java.time.LocalDateTime;
@Data
@Builder
public class NoteDTO {
    private Long id;
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantMatricule;

    private Long matiereId;
    private String matiereNom;
    private boolean hasTp;

    private Long semestreId;
    private String anneeUniversitaire;

    private Double noteDS;
    private Double noteTP;
    private Double noteExamen;
    private Double noteFusionnee;

    private boolean valideeProfesseur;
    private boolean noteComplete;
    private boolean admissible;

    private LocalDateTime dateSaisie;
    private LocalDateTime dateModification;
    public static NoteDTO fromEntity(Note note) {
        return NoteDTO.builder()
                .id(note.getId())
                .etudiantId(note.getEtudiant().getId())
                .etudiantNom(note.getEtudiant().getNom())
                .etudiantPrenom(note.getEtudiant().getPrenom())
                .etudiantMatricule(note.getEtudiant().getMatricule())
                .matiereId(note.getMatiere().getId())
                .matiereNom(note.getMatiere().getNom())
                .hasTp(note.isHasTp())
                .semestreId(note.getSemestre().getId())
                .anneeUniversitaire(note.getAnneeUniversitaire())
                .noteDS(note.getNoteDS())
                .noteTP(note.getNoteTP())
                .noteExamen(note.getNoteExamen())
                .noteFusionnee(note.getNoteFusionnee())
                .valideeProfesseur(note.isValideeProfesseur())
                .noteComplete(note.estComplete())
                .admissible(note.estAdmissible())
                .dateSaisie(note.getDateSaisie())
                .dateModification(note.getDateModification())
                .build();
    }
}
