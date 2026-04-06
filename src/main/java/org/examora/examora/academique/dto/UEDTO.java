package org.examora.examora.academique.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UEDTO {
    private Long id;
    private String nom;
    private String code;
    private Long semestreId;
    private String semestreNom;
    private Integer nombreMatieres;
}
