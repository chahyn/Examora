package org.examora.examora.statistiques.dto;

import lombok.Builder;
import lombok.Data;
import org.examora.examora.statistiques.entities.AlertePrecoce;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertePrecoceDTO {

    private Long   id;
    private Long   etudiantId;
    private String etudiantMatricule;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantClasse;
    private Long   semestreId;
    private String anneeUniversitaire;

    private AlertePrecoce.NiveauRisque niveauRisque;
    private String  message;
    private Integer nbMatieresRisque;
    private Double  moyenneDsActuelle;
    private boolean traitee;
    private LocalDateTime dateAlerte;

    public static AlertePrecoceDTO fromEntity(AlertePrecoce a) {
        return AlertePrecoceDTO.builder()
                .id(a.getId())
                .etudiantId(a.getEtudiant().getId())
                .etudiantMatricule(a.getEtudiant().getMatricule())
                .etudiantNom(a.getEtudiant().getNom())
                .etudiantPrenom(a.getEtudiant().getPrenom())
                .etudiantClasse(a.getEtudiant().getClasse())
                .semestreId(a.getSemestre().getId())
                .anneeUniversitaire(a.getAnneeUniversitaire())
                .niveauRisque(a.getNiveauRisque())
                .message(a.getMessage())
                .nbMatieresRisque(a.getNbMatieresRisque())
                .moyenneDsActuelle(a.getMoyenneDsActuelle())
                .traitee(a.isTraitee())
                .dateAlerte(a.getDateAlerte())
                .build();
    }
}