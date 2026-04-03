package org.examora.examora.examen.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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
    //read the upload directory path frome BD
    @Value("${app.upload.dir}")
    private String uploadDir;

    private final CoursDocumentRepository coursDocumentRepo;

    //when pdf submitted this returns the coursDocument entity avec id
    public CoursDocument upload(MultipartFile fichier,
                                Matiere matiere,
                                Professeur professeur) throws IOException{

        //validate pdf or not
        String contentType = fichier.getContentType();
        if(!"application/pdf".equals(contentType)){
            throw new IllegalArgumentException("seuls les fichiers PDF sont accepté");
        }
        // create a new random file name
        // moin Conflits ( si 2 profs on le meme cours nom ou path nom)
        String nomStockage = UUID.randomUUID() + ".pdf";

        // Paths.get(uploadDir) = /var/uploads/examflow
        //    .resolve(nomStockage) = /var/uploads/examflow/abc123.pdf
        Path destination = Paths.get(uploadDir).resolve(nomStockage);

        Files.createDirectories((destination.getParent()));
        Files.write(destination , fichier.getBytes());

        //finaly build and save only the metdata
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
 //prof must make the pdf visible
    public CoursDocument publier (Long id , Professeur caller)
    {
        CoursDocument doc = coursDocumentRepo.findById(id)
                .orElseThrow(()-> new RuntimeException(("Document introuvable")));

        //securité pour que seulement le prof doit publier
        if(!doc.getProfesseur().getId().equals(caller.getId())){
            throw new RuntimeException("accès refusé ");
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

    //prof peut voir tous documents
    public List<CoursDocument> findByProfesseur(Long professeurId){
        return coursDocumentRepo.findByProfesseurId(professeurId);
    }

    public List<CoursDocument> findVisibleByMatiere(Long matierId){
        return coursDocumentRepo.findByMatiereIdAndVisibleEtudiantsTrue(matierId);
    }

    //get the doc before sending to claude
    public CoursDocument findById(Long id){
        return coursDocumentRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("Document introuvable"));
    }
}

