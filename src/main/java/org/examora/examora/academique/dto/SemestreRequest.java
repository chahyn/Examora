package org.examora.examora.academique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemestreRequest {
    @NotBlank
    private String nom;
    @NotNull
    private Integer numero;
    @NotNull
    private Long filiereId;
}
