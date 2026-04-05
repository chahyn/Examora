package org.examora.examora.notes.entities;

import jakarta.persistence.*;
import lombok.*;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.utilisateur.entities.Etudiant;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "note",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_note_etudiant_matiere_semestre_annee",
                columnNames = {"etudiant_id", "matiere_id", "semestre_id", "annee_universitaire"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @Column(name = "has_tp", nullable = false)
    @Builder.Default
    private boolean hasTp = false;

    @Column(name = "note_ds")
    private Double noteDS;

    @Column(name = "note_tp")
    private Double noteTP;

    @Column(name = "note_examen")
    private Double noteExamen;

    @Column(name = "note_fusionnee")
    private Double noteFusionnee;

    @Column(name = "annee_universitaire", nullable = false, length = 9)
    private String anneeUniversitaire;

    @Column(name = "validee_professeur", nullable = false)
    @Builder.Default
    private boolean valideeProfesseur = false;

    @Column(name = "date_saisie", nullable = false, updatable = false)
    private LocalDateTime dateSaisie;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @PrePersist
    protected void onPrePersist() {
        dateSaisie       = LocalDateTime.now();
        dateModification = LocalDateTime.now();
        recalculer();
    }

    @PreUpdate
    protected void onPreUpdate() {
        dateModification = LocalDateTime.now();
        recalculer();
    }

    public void recalculer() {
        if (this.hasTp) {
            if (noteDS != null && noteTP != null && noteExamen != null) {
                noteFusionnee = arrondir(noteDS * 0.25 + noteTP * 0.25 + noteExamen * 0.50);
            }
        } else {
            if (noteDS != null && noteExamen != null) {
                noteFusionnee = arrondir(noteDS * 0.35 + noteExamen * 0.65);
            }
        }
    }

    public boolean estAdmissible() {
        return noteFusionnee != null && noteFusionnee >= 10.0;
    }

    public boolean estComplete() {
        if (this.hasTp) {
            return noteDS != null && noteTP != null && noteExamen != null;
        }
        return noteDS != null && noteExamen != null;
    }

    private static double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}
