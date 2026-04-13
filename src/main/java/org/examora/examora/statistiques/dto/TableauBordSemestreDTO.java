package org.examora.examora.statistiques.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO de synthèse renvoyé par GET /api/admin/statistiques/semestre/{id}
 * Regroupe toutes les données pour le tableau de bord.
 */
@Data
@Builder
public class TableauBordSemestreDTO {

    private Long   semestreId;
    private String semestreNom;
    private String anneeUniversitaire;

    // Résumé global
    private Integer nbEtudiants;
    private Double  moyenneGenerale;
    private Double  tauxReussiteGlobal;
    private Integer nbAlertesActives;

    // Détail par matière
    private List<StatistiquesExamenDTO> statistiquesParMatiere;

    // Matières critiques (taux < 50 %)
    private List<StatistiquesExamenDTO> matieresCritiques;

    // Alertes non traitées
    private List<AlertePrecoceDTO> alertesActives;
}