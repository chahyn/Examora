package org.examora.examora.academique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemestreDTO {
    private Long id;
    private String nom;
    private Integer numero;
    private Long filiereId;
    private String filierNom;
}
