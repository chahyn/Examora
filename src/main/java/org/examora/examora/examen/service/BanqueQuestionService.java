package org.examora.examora.examen.service;

import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.examen.entities.BanqueQuestion;
import org.examora.examora.examen.entities.Question;
import org.examora.examora.examen.repository.BanqueQuestionRepository;
import org.examora.examora.examen.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BanqueQuestionService {

    private final BanqueQuestionRepository banqueRepo;
    private final QuestionRepository questionRepo;

    // ---------------------------------------------------------------
    // GET OR CREATE — finds the bank for a subject+session.
    // If none exists yet, creates a new empty one.
    // This is safe to call multiple times — never creates duplicates
    // because of the unique constraint on (matiere_id, session_academique).
    // ---------------------------------------------------------------
    public BanqueQuestion getOuCreer(Matiere matiere, String session) {
        return banqueRepo
                .findByMatiereAndSessionAcademique(matiere, session)
                .orElseGet(() -> {
                    BanqueQuestion banque = BanqueQuestion.builder()
                            .matiere(matiere)
                            .sessionAcademique(session)
                            .build();
                    return banqueRepo.save(banque);
                });
    }

    // ---------------------------------------------------------------
    // ENREGISTRER — links a list of questions to the bank.
    // Called after Claude generates questions and they are saved.
    // ---------------------------------------------------------------
    public void enregistrer(List<Question> questions,
                            Matiere matiere,
                            String session) {

        BanqueQuestion banque = getOuCreer(matiere, session);

        questions.forEach(q -> q.setBanqueQuestion(banque));
        questionRepo.saveAll(questions);
    }

    // ---------------------------------------------------------------
    // GET ENONCES DEJA POSES — returns the text of all questions
    // already in the bank for this subject and session.
    // Passed to Claude so it never generates duplicate questions.
    // ---------------------------------------------------------------
    public List<String> getEnoncesDejaPoses(Matiere matiere, String session) {
        return banqueRepo
                .findByMatiereAndSessionAcademique(matiere, session)
                .map(banque -> banque.getQuestions()
                        .stream()
                        .map(Question::getEnonce)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public List<BanqueQuestion> findByMatiere(Long matiereId) {
        return banqueRepo.findByMatiereId(matiereId);
    }
}