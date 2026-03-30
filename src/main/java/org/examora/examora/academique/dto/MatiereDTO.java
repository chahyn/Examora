package org.examora.examora.academique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatiereDTO {
    private Long id;
    private String nom;
    private String code;
    private Integer credits;
    private Long ueId;
    private String ueNom;
    private Long professeurId;
    private String professeurNom;


}
