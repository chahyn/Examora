package org.examora.examora.notes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
@Data
public class SaisieNotesGroupeRequest {

    @NotNull
    private Long matiereId;

    @NotNull
    private Long semestreId;

    @NotNull
    private String anneeUniversitaire;

    @Valid
    @NotNull(message = "La liste des notes est obligatoire")
    private List<NoteRequest> notes;
}
