package org.examora.examora.academique.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiliereRequest {
    @NotBlank
    private String nom;
    @NotBlank
    private String code;
    @NotBlank
    private Integer dureeAnnees;
}
