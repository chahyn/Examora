package org.examora.examora.academique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalleRequest {
    @NotBlank
    private String nom;
    @NotNull
    private Integer capacite;
    private String batiment;
}
