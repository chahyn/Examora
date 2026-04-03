package org.examora.examora.examen.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.examora.examora.examen.entities.*;
import org.examora.examora.examen.repository.BanqueQuestionRepository;
import org.examora.examora.examen.repository.ExamenRepository;
import org.examora.examora.examen.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerationService {
    private final ClaudeService claudeService;
    private final ExamenRepository examenRepo;
    private final QuestionRepository questionRepo;
    private final BanqueQuestionRepository banqueRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public Examen generateExam(CoursDocument coursDocument, ExamenConfig config, String sessionAcademique){
        //save examen shell
        Examen examen = Examen.builder()
                .statut(StatutExamen.EN_ATTENTE)
                .matiere(coursDocument.getMatiere())
                .professeur(coursDocument.getProfesseur())
                .coursSource(coursDocument)
                .config(config)
                .build();
        examen = examenRepo.save(examen);
        //call claude
        try {
            int totalQuestions = config.getNbOuverte() + config.getNbQcm();
            String jsonResponse = claudeService.generateExamQuestions(
                    coursDocument.getNomFichier(),
                    config.getNbQcm(),
                    config.getNbOuverte(),
                    config.getNiveau().name()
            );
            // get Json from claude
            JsonNode root = objectMapper.readTree(jsonResponse);
            List<Question> questionsToSave = new ArrayList<>();
          // get or create banque question
            BanqueQuestion banque = banqueRepo
                    .findByMatiereIdAndSessionAcademique(coursDocument.getMatiere().getId(), sessionAcademique)
                    .orElseGet(()->banqueRepo.save(
                            BanqueQuestion.builder()
                                    .matiere(coursDocument.getMatiere())
                                    .sessionAcademique(sessionAcademique)
                                    .questions(new ArrayList<>())
                            .build()
                    ));

            //Process QCM
            JsonNode qcmNode = root.path("qcm");
            if (qcmNode.isArray()){
                for (JsonNode node : qcmNode){
                    JsonNode choix = node.path("choix");
                    Question question = Question.builder()
                            .examen(examen)
                            .banqueQuestion(banque)
                            .enonce(node.path("enonce").asText())
                            .optionA(choix.has(0) ? choix.get(0).asText() : null)
                            .optionB(choix.has(1) ? choix.get(1).asText() : null)
                            .optionC(choix.has(2) ? choix.get(2).asText() : null)
                            .optionD(choix.has(3) ? choix.get(3).asText() : null)
                            .reponseCorrecte(node.path("reponseCorrecte").asText())
                            .points(2.0)
                            .difficulte(config.getNiveau())
                            .valideeProfesseur(false)
                            .build();
                    questionsToSave.add(question);
                }
            }
            // process open question
            JsonNode openNode = root.path("openQuestions");
            if (openNode.isArray()) {
                for (JsonNode node : openNode) {
                    Question question = Question.builder()
                            .examen(examen)
                            .banqueQuestion(banque)
                            .type(TypeQuestion.OUVERTE)
                            .enonce(node.path("enonce").asText())
                            .reponseCorrecte(node.path("reponseCorrecte").asText())
                            .points(4.0)
                            .difficulte(config.getNiveau())
                            .valideeProfesseur(false)
                            .build();
                    questionsToSave.add(question);
                }
            }
            // persist all question
            questionRepo.saveAll(questionsToSave);

            // mark examen generated
            examen.setStatut(StatutExamen.GENERATED);
            examen.setQuestions(questionsToSave);
        }catch (Exception e){
            examen.setStatut(StatutExamen.ECHOUE);
            examenRepo.save(examen);
            throw new RuntimeException("Echec de la generation de l'examen" + e.getMessage(), e);
        }
        return examenRepo.save(examen);
    }

}
