package org.examora.examora.notes.entities;

import jakarta.persistence.*;
import lombok.*;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.utilisateur.entities.Etudiant;

import java.time.LocalDateTime;
@Entity
@Table(
        name = "decision_etudiant",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_decision_etudiant_semestre_annee",
                columnNames = {"etudiant_id", "semestre_id", "annee_universitaire"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DecisionEtudiant {

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

    @Column(name = "credits_semestre", nullable = false)
    @Builder.Default
    private Integer creditsSemestre = 0;

    @Column(name = "credits_annuels", nullable = false)
    @Builder.Default
    private Integer creditsAnnuels = 0;

    @Column(name = "moyenne_annuelle")
    private Double moyenneAnnuelle;


    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 30)
    private DecisionType decision;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_deliberation")
    private LocalDateTime dateDeliberation;
    @Column(name = "deliberation_finalisee", nullable = false)
    @Builder.Default
    private boolean deliberationFinalisee = false;

    public static DecisionType evaluerDecision(int creditsAnnuels, double moyenneAnnuelle) {
        if (creditsAnnuels >= 54 && moyenneAnnuelle >= 10.0) {
            return DecisionType.ADMIS;
        } else if (creditsAnnuels < 42) {
            return DecisionType.REDOUBLE;
        }
        return DecisionType.AJOURNE;
    }
}
