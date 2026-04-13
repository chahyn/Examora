package org.examora.examora.statistiques.entities;

import jakarta.persistence.*;
import lombok.*;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.utilisateur.entities.Etudiant;

import java.time.LocalDateTime;

/**
 * Alerte générée automatiquement chaque lundi pour les étudiants en difficulté.
 * Basée sur : noteDS < seuil OU moyenne < 8 sur plusieurs matières.
 */
@Entity
@Table(name = "alerte_precoce")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AlertePrecoce {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_risque", nullable = false, length = 20)
    private NiveauRisque niveauRisque;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    /** Nombre de matières avec noteDS insuffisante */
    @Column(name = "nb_matieres_risque", nullable = false)
    @Builder.Default
    private Integer nbMatieresRisque = 0;

    @Column(name = "moyenne_ds_actuelle")
    private Double moyenneDsActuelle;

    @Column(name = "traitee", nullable = false)
    @Builder.Default
    private boolean traitee = false;

    @Column(name = "date_alerte", nullable = false)
    private LocalDateTime dateAlerte;

    @PrePersist
    protected void beforePersist() {
        dateAlerte = LocalDateTime.now();
    }

    public enum NiveauRisque {
        FAIBLE,   // 1 matière en dessous du seuil
        MOYEN,    // 2 matières
        ELEVE     // 3+ matières ou moyenne globale < 8
    }
}