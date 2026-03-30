package org.examora.examora.academique.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.examora.examora.utilisateur.entities.Professeur;

@Entity
@Table(name = "matieres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false , unique = true)
    private String code;

    @Column(nullable = false)
    private Integer credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ue_id", nullable = false)
    private UE ue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id")
    private Professeur professeur;
}
