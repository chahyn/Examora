package org.examora.examora.examen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.examora.examora.examen.entities.ExamenConfig;
import org.examora.examora.examen.entities.NiveauDifficulte;
import org.examora.examora.examen.entities.Question;
import org.examora.examora.examen.entities.TypeQuestion;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaudeApiService {

    @Value("${app-k.claude.apiey}")
    private String apiKey;

    @Value("${app.claude.url}")
    private String apiUrl;

    @Value("${app.claude.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // ---------------------------------------------------------------
    // GENERER QUESTIONS — the main method.
    // Sends the course content + config to Claude and returns
    // a parsed list of Question objects ready to be saved.
    // ---------------------------------------------------------------
    public List<Question> genererQuestions(String contenuCours,
                                           ExamenConfig config,
                                           List<String> questionsExistantes) {

        String prompt = construirePrompt(contenuCours, config, questionsExistantes);

        // Build the HTTP headers Claude requires
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the request body
        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 4000,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        // Make the HTTP call
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
        );

        // Extract the text content from Claude's response structure:
        // response.body.content[0].text
        String jsonTexte = extraireTexte(response.getBody());

        return parseQuestions(jsonTexte);
    }

    // ---------------------------------------------------------------
    // CONSTRUIRE PROMPT — tells Claude exactly what to generate
    // and what format to return it in.
    // questionsExistantes prevents Claude repeating old questions.
    // ---------------------------------------------------------------
    private String construirePrompt(String cours,
                                    ExamenConfig config,
                                    List<String> questionsExistantes) {

        String eviter = questionsExistantes.isEmpty()
                ? "Aucune"
                : String.join(" | ", questionsExistantes);

        return """
                Tu es un assistant pédagogique expert. Génère un examen structuré.

                Cours :
                %s

                Paramètres :
                - Durée : %d minutes
                - Barème total : %.1f points
                - Niveau de difficulté : %s
                - Nombre de QCM : %d
                - Nombre de questions ouvertes : %d

                Questions déjà posées à ne pas répéter :
                %s

                Retourne UNIQUEMENT un JSON valide, sans texte avant ou après, avec ce format :
                {
                  "questions": [
                    {
                      "type": "QCM",
                      "enonce": "...",
                      "optionA": "...",
                      "optionB": "...",
                      "optionC": "...",
                      "optionD": "...",
                      "reponseCorrecte": "A",
                      "points": 2.0,
                      "difficulte": "MOYEN"
                    },
                    {
                      "type": "OUVERTE",
                      "enonce": "...",
                      "optionA": null,
                      "optionB": null,
                      "optionC": null,
                      "optionD": null,
                      "reponseCorrecte": null,
                      "points": 5.0,
                      "difficulte": "DIFFICILE"
                    }
                  ]
                }
                """.formatted(
                cours,
                config.getDureeMinutes(),
                config.getBareme(),
                config.getNiveau(),
                config.getNbQcm(),
                config.getNbOuverte(),
                eviter
        );
    }

    // ---------------------------------------------------------------
    // EXTRAIRE TEXTE — navigates Claude's response JSON structure
    // to get to the actual text content.
    // Structure: { content: [ { type: "text", text: "..." } ] }
    // ---------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private String extraireTexte(Map<String, Object> responseBody) {
        List<Map<String, Object>> content =
                (List<Map<String, Object>>) responseBody.get("content");

        if (content == null || content.isEmpty()) {
            throw new RuntimeException("Réponse Claude vide");
        }

        return (String) content.get(0).get("text");
    }

    // ---------------------------------------------------------------
    // PARSE QUESTIONS — converts the JSON string Claude returned
    // into actual Question entity objects.
    // Uses a wrapper class because Claude returns { "questions": [...] }
    // ---------------------------------------------------------------
    private List<Question> parseQuestions(String json) {
        try {
            // Strip markdown code fences if Claude wraps with ```json
            String jsonPropre = json
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            QuestionListWrapper wrapper = objectMapper.readValue(
                    jsonPropre,
                    QuestionListWrapper.class
            );

            return wrapper.getQuestions().stream()
                    .map(this::mapToEntity)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Erreur parsing réponse Claude : " + e.getMessage());
        }
    }

    // Converts the DTO Claude returned into a Question entity
    private Question mapToEntity(QuestionClaudeDTO dto) {
        return Question.builder()
                .type(TypeQuestion.valueOf(dto.getType()))
                .enonce(dto.getEnonce())
                .optionA(dto.getOptionA())
                .optionB(dto.getOptionB())
                .optionC(dto.getOptionC())
                .optionD(dto.getOptionD())
                .reponseCorrecte(dto.getReponseCorrecte())
                .points(dto.getPoints())
                .difficulte(NiveauDifficulte.valueOf(dto.getDifficulte()))
                .valideeProfesseur(false)
                .build();
    }

    // ---------------------------------------------------------------
    // INNER CLASSES — used only for JSON parsing, not exposed outside
    // ---------------------------------------------------------------

    @Data
    private static class QuestionListWrapper {
        private List<QuestionClaudeDTO> questions;
    }

    @Data
    private static class QuestionClaudeDTO {
        private String type;
        private String enonce;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String reponseCorrecte;
        private Double points;
        private String difficulte;
    }
}
