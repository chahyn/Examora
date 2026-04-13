package org.examora.examora.statistiques.dto;

import lombok.Builder;
import lombok.Data;
import org.examora.examora.statistiques.entities.ProgressionEtudiant;

@Data
@Builder
public class ProgressionEtudiantDTO {

    private Long   id;
    private Long   etudiantId;
    private String etudiantMatricule;
    private String etudiantNom;
    private String etudiantPrenom;
    private Long   semestreId;
    private String semestreNom;
    private String anneeUniversitaire;

    private Double  moyenneSemestre;
    private Integer creditsValides;
    private Integer rangFiliere;
    private Integer effectifFiliere;
    private Double  deltaMoyenne;
    private Integer nbMatieresEchouees;

    /** Texte lisible : "↑ +1.5" ou "↓ -2.0" ou "=" */
    private String tendance;

    public static ProgressionEtudiantDTO fromEntity(ProgressionEtudiant p) {
        String tendance = "=";
        if (p.getDeltaMoyenne() != null) {
            if (p.getDeltaMoyenne() > 0.5)       tendance = "↑ +" + String.format("%.1f", p.getDeltaMoyenne());
            else if (p.getDeltaMoyenne() < -0.5)  tendance = "↓ "  + String.format("%.1f", p.getDeltaMoyenne());
        }
        return ProgressionEtudiantDTO.builder()
                .id(p.getId())
                .etudiantId(p.getEtudiant().getId())
                .etudiantMatricule(p.getEtudiant().getMatricule())
                .etudiantNom(p.getEtudiant().getNom())
                .etudiantPrenom(p.getEtudiant().getPrenom())
                .semestreId(p.getSemestre().getId())
                .semestreNom(p.getSemestre().getNom())
                .anneeUniversitaire(p.getAnneeUniversitaire())
                .moyenneSemestre(p.getMoyenneSemestre())
                .creditsValides(p.getCreditsValides())
                .rangFiliere(p.getRangFiliere())
                .effectifFiliere(p.getEffectifFiliere())
                .deltaMoyenne(p.getDeltaMoyenne())
                .nbMatieresEchouees(p.getNbMatieresEchouees())
                .tendance(tendance)
                .build();
    }
}
