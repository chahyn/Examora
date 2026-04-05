package org.examora.examora.notes.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class ReleveNotesDTO {
    private Long etudiantId;
    private String nom;
    private String prenom;
    private String matricule;
    private String filiere;
    private Integer anneeEtude;
    private String nomSemestre;
    private String anneeUniversitaire;
    private List<MatiereNoteDTO> matieres;
    private Double moyenneSemestre;
    private Integer creditsObtenusSemestre;
    private Integer creditsAnnuels;
    private Double moyenneAnnuelle;

    private String decision;
    private boolean deliberationFinalisee;

    @Data
    @Builder
    public static class MatiereNoteDTO {
        private String matiereNom;
        private boolean hasTp;
        private Double noteDS;
        private Double noteTP;
        private Double noteExamen;
        private Double noteFusionnee;
        private boolean admissible;
        private Double coefficient;
        private String nomUE;
        private Integer creditsUE;
    }
}
