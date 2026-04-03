package org.examora.examora.examen.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.examora.examora.examen.dto.ExamenCreateRequest;
import org.examora.examora.examen.dto.ExamenDto;
import org.examora.examora.examen.dto.ExamenMapper;
import org.examora.examora.examen.entities.CoursDocument;
import org.examora.examora.examen.entities.Examen;
import org.examora.examora.examen.entities.ExamenConfig;
import org.examora.examora.examen.entities.StatutExamen;
import org.examora.examora.examen.repository.CoursDocumentRepository;
import org.examora.examora.examen.service.ExamenService;
import org.examora.examora.examen.service.GenerationService;
import org.examora.examora.security.AuthHelper;
import org.examora.examora.utilisateur.entities.Professeur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/examens")
@RequiredArgsConstructor
public class ExamenController {
    private final ExamenService examenService;
    private final GenerationService generationService;
    private final CoursDocumentRepository coursDocumentRepo;
    private final ExamenMapper examenMapper;
    private final AuthHelper authHelper;

    @PostMapping("/generer")
    public ResponseEntity<ExamenDto> generer(
            @RequestBody ExamenCreateRequest request,
            HttpServletRequest httpRequest) {

        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);

        CoursDocument coursDocument = coursDocumentRepo.findById(request.getCoursDocumentId())
                .orElseThrow(() -> new RuntimeException("Document cours introuvable"));

        // Security: professor can only generate from their own documents
        if (!coursDocument.getProfesseur().getId().equals(professeur.getId())) {
            throw new RuntimeException("Accès refusé à ce document");
        }

        ExamenConfig config = examenMapper.toConfigEntity(request.getConfig());

        Examen examen = generationService.generateExam(
                coursDocument,
                config,
                request.getSessionAcademique()
        );

        // Set optional schedule if provided at creation time
        if (request.getDateDebut() != null) {
            examen = examenService.planifier(examen.getId(), request.getDateDebut());
        }

        return ResponseEntity.ok(examenMapper.toDto(examen));
    }

    /**
     * GET /api/examens/mes-examens
     * Professor sees all their exams
     */
    @GetMapping("/mes-examens")
    public ResponseEntity<List<ExamenDto>> mesExamens(HttpServletRequest httpRequest) {
        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        List<Examen> examens = examenService.findByProfesseur(professeur.getId());
        return ResponseEntity.ok(examenMapper.toDtoList(examens));
    }

    /**
     * GET /api/examens/mes-examens?statut=VALIDE
     * Professor filters their exams by status
     */
    @GetMapping("/mes-examens/statut/{statut}")
    public ResponseEntity<List<ExamenDto>> mesExamensByStatut(
            @PathVariable StatutExamen statut,
            HttpServletRequest httpRequest) {
        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        List<Examen> examens = examenService.findByProfesseurAndStatut(professeur.getId(), statut);
        return ResponseEntity.ok(examenMapper.toDtoList(examens));
    }

    /**
     * GET /api/examens/{id}
     * Professor gets full exam details (with questions)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExamenDto> getById(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        Examen examen = examenService.findById(id);
        if (!examen.getProfesseur().getId().equals(professeur.getId())) {
            throw new RuntimeException("Accès refusé");
        }
        return ResponseEntity.ok(examenMapper.toDto(examen));
    }

    /**
     * PUT /api/examens/{id}/valider
     * Professor validates the exam (after reviewing all questions)
     */
    @PutMapping("/{id}/valider")
    public ResponseEntity<ExamenDto> valider(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        Examen examen = examenService.valider(id, professeur);
        return ResponseEntity.ok(examenMapper.toDto(examen));
    }

    /**
     * PUT /api/examens/{id}/planifier
     * Professor schedules the exam with a start date
     */
    @PutMapping("/{id}/planifier")
    public ResponseEntity<ExamenDto> planifier(
            @PathVariable Long id,
            @RequestParam String dateDebut,
            HttpServletRequest httpRequest) {
        authHelper.getCurrentProfesseur(httpRequest);
        Examen examen = examenService.planifier(id, LocalDateTime.parse(dateDebut));
        return ResponseEntity.ok(examenMapper.toDto(examen));
    }

    /**
     * PUT /api/examens/{id}/terminer
     * Professor manually marks exam as finished
     */
    @PutMapping("/{id}/terminer")
    public ResponseEntity<ExamenDto> terminer(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        authHelper.getCurrentProfesseur(httpRequest);
        Examen examen = examenService.terminer(id);
        return ResponseEntity.ok(examenMapper.toDto(examen));
    }

    // ── ETUDIANT ──────────────────────────────────────────────────

    /**
     * GET /api/examens/etudiant/en-cours?matiereId=1
     * Etudiant sees live exams for their subject
     */
    @GetMapping("/etudiant/en-cours")
    public ResponseEntity<List<ExamenDto>> enCoursForEtudiant(
            @RequestParam Long matiereId,
            HttpServletRequest httpRequest) {
        authHelper.getCurrentEtudiant(httpRequest);
        List<Examen> examens = examenService.findEnCoursForEtudiant(matiereId);
        return ResponseEntity.ok(examenMapper.toDtoList(examens));
    }

    /**
     * GET /api/examens/etudiant/accessibles?matiereId=1
     * Etudiant sees planned + finished exams (results access after exam ends)
     */
    @GetMapping("/etudiant/accessibles")
    public ResponseEntity<List<ExamenDto>> accessiblesForEtudiant(
            @RequestParam Long matiereId,
            HttpServletRequest httpRequest) {
        authHelper.getCurrentEtudiant(httpRequest);
        List<Examen> examens = examenService.findAccessiblesByEtudiant(matiereId);
        return ResponseEntity.ok(examenMapper.toDtoList(examens));
    }
}
