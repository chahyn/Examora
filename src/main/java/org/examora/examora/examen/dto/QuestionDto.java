package org.examora.examora.examen.dto;

import lombok.Data;
import org.examora.examora.examen.entities.NiveauDifficulte;
import org.examora.examora.examen.entities.TypeQuestion;

@Data
public class QuestionDto {
    private Long id;
    private TypeQuestion type;
    private String enonce;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String reponseCorrecte;
    private Double points;
    private NiveauDifficulte difficulte;
    private boolean valideeProfesseur;
}
