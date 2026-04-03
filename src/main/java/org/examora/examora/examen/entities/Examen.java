package org.examora.examora.examen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.utilisateur.entities.Professeur;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Examen")

public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "cours_document_id")
    private CoursDocument coursSource;

    @Embedded
    private ExamenConfig config;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutExamen statut = StatutExamen.EN_ATTENTE;

    private LocalDateTime dateCreation;

    //student access Control
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    private Double noteTotale;

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variante> variantes = new ArrayList<>();

    @PrePersist
    public void avantSauvegarde() {
        this.dateCreation = LocalDateTime.now();
    }
    public boolean estEnCours() {
        if (dateDebut == null || dateFin == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(dateDebut) && now.isBefore(dateFin);
    }

    // Has the exam window passed entirely?
    public boolean estTermine() {
        if (dateFin == null) return false;
        return LocalDateTime.now().isAfter(dateFin);
    }
}
