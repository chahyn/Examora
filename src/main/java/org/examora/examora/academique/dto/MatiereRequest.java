package org.examora.examora.academique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatiereRequest {
    @NotBlank
    private String nom;
    @NotBlank
    private String code;
    @NotNull
    private Integer credits;
    @NotNull
    private Long ueId;
    private Long professeurId;

}
