package org.examora.examora.examen.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CoursDocumentDto {
    private Long id;
    private String nomFichier;
    private String cheminStockage;        // ← must exist
    private Long tailleFichierOctets;     // ← must exist
    private LocalDateTime dateUpload;
    private boolean visibleEtudiants;
    private Boolean traiteParIA;
    private Long matiereId;
    private String matiereNom;            // ← must exist
    private Long professeurId;
    private String professeurNom;

}
