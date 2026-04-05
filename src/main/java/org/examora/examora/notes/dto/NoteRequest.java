package org.examora.examora.notes.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NoteRequest {

    @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "L'identifiant de la matière est obligatoire")
    private Long matiereId;

    @NotNull(message = "L'identifiant du semestre est obligatoire")
    private Long semestreId;

    @NotBlank(message = "L'année universitaire est obligatoire (ex: 2025-2026)")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Format attendu : AAAA-AAAA")
    private String anneeUniversitaire;

    private boolean hasTp = false;

    @DecimalMin(value = "0.0", message = "La note DS doit être >= 0")
    @DecimalMax(value = "20.0", message = "La note DS doit être <= 20")
    private Double noteDS;

    @DecimalMin(value = "0.0", message = "La note TP doit être >= 0")
    @DecimalMax(value = "20.0", message = "La note TP doit être <= 20")
    private Double noteTP;

    @DecimalMin(value = "0.0", message = "La note d'examen doit être >= 0")
    @DecimalMax(value = "20.0", message = "La note d'examen doit être <= 20")
    private Double noteExamen;
}
