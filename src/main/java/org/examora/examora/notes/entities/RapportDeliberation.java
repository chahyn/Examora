package org.examora.examora.notes.entities;

import jakarta.persistence.*;
import lombok.*;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.utilisateur.entities.Utilisateur;

import java.time.LocalDateTime;
@Entity
@Table(
        name = "rapport_deliberation",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rapport_semestre_annee",
                columnNames = {"semestre_id", "annee_universitaire"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RapportDeliberation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(name = "annee_universitaire", nullable = false, length = 9)
    private String anneeUniversitaire;

    @Column(name = "date_deliberation", nullable = false)
    private LocalDateTime dateDeliberation;

    @Column(name = "nb_etudiants", nullable = false)
    @Builder.Default
    private Integer nbEtudiants = 0;

    @Column(name = "nb_admis", nullable = false)
    @Builder.Default
    private Integer nbAdmis = 0;

    @Column(name = "nb_ajournes", nullable = false)
    @Builder.Default
    private Integer nbAjournes = 0;

    @Column(name = "nb_redoubles", nullable = false)
    @Builder.Default
    private Integer nbRedoubles = 0;

    @Column(name = "taux_reussite")
    private Double tauxReussite;

    @Column(name = "moyenne_promo")
    private Double moyennePromo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genere_par")
    private Utilisateur generePar;

    @PrePersist
    protected void onPrePersist() {
        if (dateDeliberation == null) dateDeliberation = LocalDateTime.now();
        calculerTaux();
    }

    @PreUpdate
    protected void onPreUpdate() {
        calculerTaux();
    }

    private void calculerTaux() {
        if (nbEtudiants != null && nbEtudiants > 0 && nbAdmis != null) {
            tauxReussite = Math.round((nbAdmis * 100.0 / nbEtudiants) * 100.0) / 100.0;
        }
    }
}
