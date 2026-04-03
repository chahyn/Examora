package org.examora.examora.examen.dto;

import lombok.Data;
import org.examora.examora.examen.entities.NiveauDifficulte;
import org.examora.examora.examen.entities.TypeQuestion;

@Data
public class ExamenConfigDto {
    private Integer dureeMinutes;
    private Double bareme;
    private Integer nbQcm;
    private Integer nbOuverte;
    private NiveauDifficulte niveau;
    private TypeQuestion type;
}
