package org.examora.examora.examen.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamenCreateRequest {

    @NotNull(message = "L'ID du document cours est obligatoire")
    private Long coursDocumentId;

    @NotNull(message = "La configuration est obligatoire")
    @Valid
    private ExamenConfigDto config;

    @NotNull(message = "La session académique est obligatoire")
    private String sessionAcademique;

    // Optional — professor can schedule the exam window at creation time
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
}
