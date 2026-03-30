package org.examora.examora.examen.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamenConfig {
    private Integer dureeMinutes;
    private Double bareme;
    private Integer nbQcm;
    private Integer nbOuverte;
    @Enumerated(EnumType.STRING)
    private NiveauDifficulte niveau;
}
