package org.examora.examora.examen.dto;

import lombok.Data;
import org.examora.examora.examen.entities.StatutExamen;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamenDto {
    private Long id;
    private Long matiereId;
    private String matiereNom;
    private Long professeurId;
    private String professeurNom;
    private Long coursSourceId;
    private String coursSourceNom;
    private ExamenConfigDto config;
    private StatutExamen statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private Double noteTotale;
    private boolean enCours;
    private boolean termine;
    private List<QuestionDto> questions;
}
