package org.examora.examora.examen.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClaudeService {
    private final ChatClient chatClient;


    //
    public String ask(String prompt){
        return chatClient
                .prompt()//build request
                .user(prompt)// set user message
                .call() // send to claude
                .content(); // get response
    }

    // methode pour generation d'examen
    public String generateExamQuestions(String courseText,
                                        int nombreQcm,
                                        int nombreOuvertes,
                                        String difficulte){
        String prompt = """
                Tu es un assistant pédagogique expert.
                Génère exactement %d questions QCM et %d questions ouvertes,
                avec un niveau de difficulté %s,
                basées sur ce cours :

                %s

                Réponds UNIQUEMENT en JSON valide avec ce format exact :
                {
                  "qcm": [
                    {
                      "enonce": "...",
                      "choix": ["A. ...", "B. ...", "C. ...", "D. ..."],
                      "reponseCorrecte": "A",
                      "explication": "...",
                      "difficulte": "%s",
                      "points": 2
                    }
                  ],
                  "openQuestions": [
                    {
                      "enonce": "...",
                      "reponseCorrecte": "...",
                      "explication": "...",
                      "difficulte": "%s",
                      "points": 4
                    }
                  ]
                }
                Ne génère rien d'autre que ce JSON.
                """.formatted(nombreQcm, nombreOuvertes, difficulte, courseText, difficulte, difficulte);

        return ask(prompt);
    }
}
