package org.examora.examora.examen.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.examora.examora.examen.dto.ExamenMapper;
import org.examora.examora.examen.dto.QuestionDto;
import org.examora.examora.examen.entities.Question;
import org.examora.examora.examen.service.ExamenService;
import org.examora.examora.security.AuthHelper;
import org.examora.examora.utilisateur.entities.Professeur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/examen/{examenId}/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final ExamenService examenService;
    private final ExamenMapper examenMapper;
    private final AuthHelper authHelper;

    /**
     * PUT /api/examens/{examenId}/questions/{questionId}/valider
     * Professor approves a single Claude-generated question
     */
    @PutMapping("/{questionId}/valider")
    public ResponseEntity<QuestionDto> validerQuestion(
            @PathVariable Long examenId,
            @PathVariable Long questionId,
            HttpServletRequest httpRequest) {

        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        Question question = examenService.validerQuestion(examenId, questionId, professeur);
        return ResponseEntity.ok(examenMapper.toQuestionDto(question));
    }
}
