package org.examora.examora.examen.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.examora.examora.examen.dto.ExamenMapper;
import org.examora.examora.examen.dto.QuestionDto;
import org.examora.examora.examen.entities.BanqueQuestion;
import org.examora.examora.examen.repository.BanqueQuestionRepository;
import org.examora.examora.examen.service.BanqueQuestionService;
import org.examora.examora.security.AuthHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banque")
@RequiredArgsConstructor
public class BanqueQuestionController {
    private final BanqueQuestionRepository banqueQuestionRepo;
    private final BanqueQuestionService banqueQuestionService;
    private final ExamenMapper examenMapper;
    private final AuthHelper authHelper;

    /**
     * GET /api/banque/matiere/{matiereId}?session=2024-2025
     * Professor browses the question bank for a subject + session
     */
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<List<QuestionDto>> getByMatiere(
            @PathVariable Long matiereId,
            @RequestParam String session,
            HttpServletRequest httpRequest) {

        authHelper.getCurrentProfesseur(httpRequest);
        BanqueQuestion banque = banqueQuestionRepo
                .findByMatiereIdAndSessionAcademique(matiereId, session)
                .orElseThrow(()-> new RuntimeException("Banque introuvable pour cette matiere"));
        List<QuestionDto> questions = banque.getQuestions()
                .stream()
                .map(examenMapper::toQuestionDto)
                .toList();
        return ResponseEntity.ok(questions);
    }
}
