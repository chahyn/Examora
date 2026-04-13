package org.examora.examora.statistiques.dto;

import lombok.Builder;
import lombok.Data;
import org.examora.examora.statistiques.entities.StatistiquesExamen;

import java.time.LocalDateTime;

@Data
@Builder
public class StatistiquesExamenDTO {

    private Long   id;
    private Long   matiereId;
    private String matiereNom;
    private String matiereCode;
    private Long   semestreId;
    private String semestreNom;
    private String anneeUniversitaire;

    // Effectifs
    private Integer nbInscrits;
    private Integer nbNotesValidees;

    // Résultats
    private Double tauxReussite;       // 0.0 – 1.0
    private Double tauxReussitePct;    // 0 – 100 (calculé)
    private Double moyenneClasse;
    private Double noteMin;
    private Double noteMax;
    private Double ecartType;

    // Distribution
    private Integer nbMoins5;
    private Integer nb5a10;
    private Integer nb10a14;
    private Integer nb14a17;
    private Integer nb17Plus;

    private LocalDateTime dateCalcul;

    public static StatistiquesExamenDTO fromEntity(StatistiquesExamen e) {
        return StatistiquesExamenDTO.builder()
                .id(e.getId())
                .matiereId(e.getMatiere().getId())
                .matiereNom(e.getMatiere().getNom())
                .matiereCode(e.getMatiere().getCode())
                .semestreId(e.getSemestre().getId())
                .semestreNom(e.getSemestre().getNom())
                .anneeUniversitaire(e.getAnneeUniversitaire())
                .nbInscrits(e.getNbInscrits())
                .nbNotesValidees(e.getNbNotesValidees())
                .tauxReussite(e.getTauxReussite())
                .tauxReussitePct(e.getTauxReussite() != null
                        ? Math.round(e.getTauxReussite() * 1000.0) / 10.0
                        : null)
                .moyenneClasse(e.getMoyenneClasse())
                .noteMin(e.getNoteMin())
                .noteMax(e.getNoteMax())
                .ecartType(e.getEcartType())
                .nbMoins5(e.getNbMoins5())
                .nb5a10(e.getNb5a10())
                .nb10a14(e.getNb10a14())
                .nb14a17(e.getNb14a17())
                .nb17Plus(e.getNb17Plus())
                .dateCalcul(e.getDateCalcul())
                .build();
    }
}
