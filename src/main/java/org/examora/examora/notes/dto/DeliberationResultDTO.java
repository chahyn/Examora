package org.examora.examora.notes.dto;
import lombok.Builder;
import lombok.Data;
import org.examora.examora.notes.entities.DecisionEtudiant;
import org.examora.examora.notes.entities.DecisionType;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
public class DeliberationResultDTO {

    private Long semestreId;
    private String nomSemestre;
    private String anneeUniversitaire;
    private LocalDateTime dateDeliberation;
    private int nbEtudiants;
    private int nbAdmis;
    private int nbAjournes;
    private int nbRedoubles;
    private double tauxReussite;
    private double moyennePromo;
    private List<EtudiantDecisionDTO> decisions;
    @Data
    @Builder
    public static class EtudiantDecisionDTO {
        private Long etudiantId;
        private String nom;
        private String prenom;
        private String matricule;
        private Double moyenneSemestre;
        private Integer creditsSemestre;
        private Integer creditsAnnuels;
        private Double moyenneAnnuelle;
        private DecisionType decision;
        private String commentaire;

        public static EtudiantDecisionDTO fromEntity(DecisionEtudiant d) {
            return EtudiantDecisionDTO.builder()
                    .etudiantId(d.getEtudiant().getId())
                    .nom(d.getEtudiant().getNom())
                    .prenom(d.getEtudiant().getPrenom())
                    .matricule(d.getEtudiant().getMatricule())
                    .moyenneSemestre(d.getMoyenneSemestre())
                    .creditsSemestre(d.getCreditsSemestre())
                    .creditsAnnuels(d.getCreditsAnnuels())
                    .moyenneAnnuelle(d.getMoyenneAnnuelle())
                    .decision(d.getDecision())
                    .commentaire(d.getCommentaire())
                    .build();
        }
    }
}
