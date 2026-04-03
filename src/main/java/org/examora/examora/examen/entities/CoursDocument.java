package org.examora.examora.examen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.utilisateur.entities.Professeur;

import java.time.LocalDateTime;

@Entity
@Table(name="documents cours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomFichier;

    @Column(nullable = false, unique = true)
    private String cheminStockage;

    private Long tailleFichierOctets;

    @Column(nullable = false)
    private LocalDateTime dateUpload;
    //the professor has the access to make the pdf (cours) seen by students

    @Column(nullable = false)
    private boolean visibleEtudiants=false;

    //subject this course belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable=false)
    private Matiere matiere;

    //who uploaded the pdf
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "professeur_id", nullable=false)
    private Professeur professeur;

    // is the text already extracted to claude
    @Column(nullable = false)
    private Boolean traiteParIA= false;

    @PrePersist
    public void avantSauvegarde(){
        this.dateUpload = LocalDateTime.now();
    }


}
