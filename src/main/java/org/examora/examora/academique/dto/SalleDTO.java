package org.examora.examora.academique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalleDTO {
    private Long id;
    private String nom;
    private Integer capacite;
    private boolean disponible;
    private String batiment;
}
