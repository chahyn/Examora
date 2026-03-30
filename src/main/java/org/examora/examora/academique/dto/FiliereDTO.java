package org.examora.examora.academique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiliereDTO {
    private Long id;
    private String nom;
    private String code;
    private Integer dureeAnnees;
    private Integer nombreSemestres;
}
