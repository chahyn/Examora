package org.examora.examora.statistiques.entities;

import jakarta.persistence.*;
import lombok.*;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.entities.Semestre;

import java.time.LocalDateTime;

/**
 * Snapshot des statistiques calculées pour une matière/semestre/année.
 * Recalculé à chaque validation de notes.
 */
@Entity
@Table(
        name = "statistiques_examen",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_stats_matiere_semestre_annee",
                columnNames = {"matiere_id", "semestre_id", "annee_universitaire"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StatistiquesExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(name = "annee_universitaire", nullable = false, length = 9)
    private String anneeUniversitaire;

    // ── Effectifs ─────────────────────────────────────────────────────────────
    @Column(name = "nb_inscrits", nullable = false)
    @Builder.Default
    private Integer nbInscrits = 0;

    @Column(name = "nb_notes_validees", nullable = false)
    @Builder.Default
    private Integer nbNotesValidees = 0;

    // ── Résultats ─────────────────────────────────────────────────────────────
    @Column(name = "taux_reussite")
    private Double tauxReussite;          // 0.0 – 1.0

    @Column(name = "moyenne_classe")
    private Double moyenneClasse;

    @Column(name = "note_min")
    private Double noteMin;

    @Column(name = "note_max")
    private Double noteMax;

    @Column(name = "ecart_type")
    private Double ecartType;

    // ── Distribution par tranches ──────────────────────────────────────────────
    /** < 5  */
    @Column(name = "nb_moins_5")   @Builder.Default private Integer nbMoins5   = 0;
    /** 5 – 9.99 */
    @Column(name = "nb_5_10")      @Builder.Default private Integer nb5a10     = 0;
    /** 10 – 13.99 */
    @Column(name = "nb_10_14")     @Builder.Default private Integer nb10a14    = 0;
    /** 14 – 16.99 */
    @Column(name = "nb_14_17")     @Builder.Default private Integer nb14a17    = 0;
    /** ≥ 17 */
    @Column(name = "nb_17_plus")   @Builder.Default private Integer nb17Plus   = 0;

    // ── Méta ──────────────────────────────────────────────────────────────────
    @Column(name = "date_calcul", nullable = false)
    private LocalDateTime dateCalcul;

    @PrePersist
    @PreUpdate
    protected void beforeSave() {
        dateCalcul = LocalDateTime.now();
    }
}