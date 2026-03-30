package org.examora.examora.examen.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.examora.examora.examen.entities.*;
import org.examora.examora.examen.repository.QuestionRepository;
import org.examora.examora.examen.repository.VarianteRepository;
import org.examora.examora.utilisateur.entities.Professeur;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenerationIAService {

    private final CoursDocumentService coursDocumentService;
    private final ExamenService examenService;
    private final ClaudeApiService claudeApiService;
    private final BanqueQuestionService banqueQuestionService;
    private final QuestionRepository questionRepo;
    private final VarianteRepository varianteRepo;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ---------------------------------------------------------------
    // GENERER — the full orchestration flow.
    // This is the single method the controller calls.
    // It coordinates every other service in the right order.
    // ---------------------------------------------------------------
    @Transactional
    public Examen generer(Long coursDocumentId,
                          Professeur professeur,
                          ExamenConfig config,
                          String sessionAcademique) throws IOException {

        // Step 1 — Load the document metadata from DB
        CoursDocument doc = coursDocumentService.findById(coursDocumentId);

        // Step 2 — Read the actual PDF bytes from disk and extract text
        Path filePath = Paths.get(uploadDir).resolve(doc.getCheminStockage());
        String contenuCours = extraireTextePDF(filePath);

        // Step 3 — Get questions already asked for this subject+session
        // so Claude does not repeat them
        List<String> dejaPoses = banqueQuestionService
                .getEnoncesDejaPoses(doc.getMatiere(), sessionAcademique);

        // Step 4 — Call Claude API and get back parsed Question objects
        List<Question> questions = claudeApiService
                .genererQuestions(contenuCours, config, dejaPoses);

        // Step 5 — Create and save the Examen entity first
        // (questions need an examen_id FK to be saved)
        Examen examen = examenService.creer(
                doc.getMatiere(),
                professeur,
                config,
                doc
        );

        // Step 6 — Link each question to the exam and save
        questions.forEach(q -> q.setExamen(examen));
        List<Question> questionsSauvegardees = questionRepo.saveAll(questions);

        // Step 7 — Store questions in the bank to prevent future repetition
        banqueQuestionService.enregistrer(
                questionsSauvegardees,
                doc.getMatiere(),
                sessionAcademique
        );

        // Step 8 — Generate anti-cheat variants (A, B, C)
        genererVariantes(examen, questionsSauvegardees);

        // Step 9 — Mark the document as processed
        coursDocumentService.marquerTraiteParIA(coursDocumentId);

        // Step 10 — Reload and return the full exam with all relations
        return examenService.findById(examen.getId());
    }

    // ---------------------------------------------------------------
    // GENERER VARIANTES — creates 3 shuffled copies of the question list.
    // Each variant gets the same questions in a different random order.
    // ---------------------------------------------------------------
    private void genererVariantes(Examen examen, List<Question> questions) {

        List<TypeVariante> codes = List.of(
                TypeVariante.A,
                TypeVariante.B,
                TypeVariante.C
        );

        List<Variante> variantes = new ArrayList<>();

        for (TypeVariante code : codes) {
            // Create a new shuffled copy of the question list
            List<Question> melangees = new ArrayList<>(questions);
            Collections.shuffle(melangees);

            Variante variante = Variante.builder()
                    .examen(examen)
                    .codeVariante(code)
                    .questions(melangees)
                    .build();

            variantes.add(variante);
        }

        varianteRepo.saveAll(variantes);
    }

    // ---------------------------------------------------------------
    // EXTRAIRE TEXTE PDF — reads the PDF from disk and extracts
    // all text content using Apache PDFBox.
    // Make sure pdfbox dependency is in your pom.xml.
    // ---------------------------------------------------------------
    private String extraireTextePDF(Path filePath) throws IOException {
        try (PDDocument document = PDDocument.load(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}