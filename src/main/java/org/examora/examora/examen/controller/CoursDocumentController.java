package org.examora.examora.examen.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.repository.MatiereRepository;
import org.examora.examora.examen.dto.CoursDocumentDto;
import org.examora.examora.examen.dto.ExamenMapper;
import org.examora.examora.examen.entities.CoursDocument;
import org.examora.examora.examen.service.CoursDocumentService;
import org.examora.examora.security.AuthHelper;
import org.examora.examora.utilisateur.entities.Professeur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class CoursDocumentController {
    private final CoursDocumentService coursDocumentService;
    private final ExamenMapper examenMapper;
    private final AuthHelper authHelper;
    private final MatiereRepository matiereRepo;

    /**
     * POST /api/documents/upload
     * Professor uploads a PDF — text is extracted and stored automatically
     */
    @PostMapping("/upload")
    public ResponseEntity<CoursDocumentDto> upload(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam Long matiereId,
            HttpServletRequest httpRequest) throws IOException {

        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        Matiere matiere = matiereRepo.findById(matiereId)
                .orElseThrow(()-> new RuntimeException("matiere introuvable"));
        CoursDocument doc = coursDocumentService.upload(fichier, matiere, professeur);
        return ResponseEntity.ok(examenMapper.toCoursDocumentDto(doc));
    }

    /**
     * GET /api/documents/mes-documents
     * Professor sees all their uploaded documents
     */
    @GetMapping("/mes-documents")
    public ResponseEntity<List<CoursDocumentDto>> mesDocuments(HttpServletRequest httpRequest) {
        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        List<CoursDocument> docs = coursDocumentService.findByProfesseur(professeur.getId());
        return ResponseEntity.ok(
                docs.stream().map(examenMapper::toCoursDocumentDto).toList()
        );
    }

    /**
     * PUT /api/documents/{id}/visibilite
     * Professor toggles student visibility of a document
     */
    @PutMapping("/{id}/visibilite")
    public ResponseEntity<CoursDocumentDto> toggleVisibilite(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Professeur professeur = authHelper.getCurrentProfesseur(httpRequest);
        CoursDocument doc = coursDocumentService.toggleVisibilite(id, professeur);
        return ResponseEntity.ok(examenMapper.toCoursDocumentDto(doc));
    }

    /**
     * GET /api/documents/matiere/{matiereId}
     * Etudiant sees visible documents for a subject
     */
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<List<CoursDocumentDto>> visiblesParMatiere(
            @PathVariable Long matiereId,
            HttpServletRequest httpRequest) {
        authHelper.getCurrentUser(httpRequest); // both roles allowed
        List<CoursDocument> docs = coursDocumentService.findVisiblesByMatiere(matiereId);
        return ResponseEntity.ok(
                docs.stream().map(examenMapper::toCoursDocumentDto).toList()
        );
    }
}
