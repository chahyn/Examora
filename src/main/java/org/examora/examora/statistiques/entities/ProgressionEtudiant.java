package org.examora.examora.statistiques.entities;

import jakarta.persistence.*;
import lombok.*;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.utilisateur.entities.Etudiant;

import java.time.LocalDateTime;

/**
 * Historique de progression d'un étudiant semestre par semestre.
 * Un enregistrement par (étudiant, semestre, annéeUniversitaire).
 */
@Entity
@Table(
        name = "progression_etudiant",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_progression_etudiant_semestre_annee",
                columnNames = {"etudiant_id", "semestre_id", "annee_universitaire"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProgressionEtudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(name = "annee_universitaire", nullable = false, length = 9)
    private String anneeUniversitaire;

    @Column(name = "moyenne_semestre")
    private Double moyenneSemestre;

    @Column(name = "credits_valides", nullable = false)
    @Builder.Default
    private Integer creditsValides = 0;

    /** Rang de l'étudiant dans sa filière pour ce semestre (1 = meilleur) */
    @Column(name = "rang_filiere")
    private Integer rangFiliere;

    /** Nombre total d'étudiants dans la filière pour ce semestre */
    @Column(name = "effectif_filiere")
    private Integer effectifFiliere;

    /** Tendance : positif = progression, négatif = régression (vs semestre précédent) */
    @Column(name = "delta_moyenne")
    private Double deltaMoyenne;

    @Column(name = "nb_matieres_echouees", nullable = false)
    @Builder.Default
    private Integer nbMatieresEchouees = 0;

    @Column(name = "date_calcul", nullable = false)
    private LocalDateTime dateCalcul;

    @PrePersist
    @PreUpdate
    protected void beforeSave() {
        dateCalcul = LocalDateTime.now();
    }
}