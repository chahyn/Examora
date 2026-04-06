package org.examora.examora.examen.service;

import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.examen.entities.CoursDocument;
import org.examora.examora.examen.repository.CoursDocumentRepository;
import org.examora.examora.utilisateur.entities.Professeur;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoursDocumentService {

    @Value("/var/uploads/examflow")
    private String uploadDir;

    private final CoursDocumentRepository coursDocumentRepo;

    public CoursDocument upload(MultipartFile fichier,
                                Matiere matiere,
                                Professeur professeur) throws IOException {
        String contentType = fichier.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont acceptés");
        }

        String nomStockage = UUID.randomUUID() + ".pdf";
        Path destination = Paths.get(uploadDir).resolve(nomStockage);
        Files.createDirectories(destination.getParent());
        Files.write(destination, fichier.getBytes());

        CoursDocument doc = CoursDocument.builder()
                .nomFichier(fichier.getOriginalFilename())
                .cheminStockage(nomStockage)
                .tailleFichierOctets(fichier.getSize())
                .matiere(matiere)
                .professeur(professeur)
                .visibleEtudiants(false)
                .traiteParIA(false)
                .build();
        return coursDocumentRepo.save(doc);
    }

    // ← ADDED: toggle on/off (replaces publier which only set to true)
    public CoursDocument toggleVisibilite(Long id, Professeur caller) {
        CoursDocument doc = coursDocumentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));

        if (!doc.getProfesseur().getId().equals(caller.getId())) {
            throw new RuntimeException("Accès refusé");
        }

        doc.setVisibleEtudiants(!doc.isVisibleEtudiants()); // true→false or false→true
        return coursDocumentRepo.save(doc);
    }

    // kept for explicit publish-only use case
    public CoursDocument publier(Long id, Professeur caller) {
        CoursDocument doc = coursDocumentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));

        if (!doc.getProfesseur().getId().equals(caller.getId())) {
            throw new RuntimeException("Accès refusé");
        }

        doc.setVisibleEtudiants(true);
        return coursDocumentRepo.save(doc);
    }

    public Resource telecharger(Long id) throws IOException {
        CoursDocument doc = coursDocumentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));

        Path filePath = Paths.get(uploadDir).resolve(doc.getCheminStockage());
        UrlResource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Fichier introuvable sur le serveur");
        }
        return resource;
    }

    public List<CoursDocument> findByProfesseur(Long professeurId) {
        return coursDocumentRepo.findByProfesseurId(professeurId);
    }

    // ← FIXED: renamed to match controller call (findVisiblesByMatiere)
    public List<CoursDocument> findVisiblesByMatiere(Long matiereId) {
        return coursDocumentRepo.findByMatiereIdAndVisibleEtudiantsTrue(matiereId);
    }

    public CoursDocument findById(Long id) {
        return coursDocumentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
    }
}