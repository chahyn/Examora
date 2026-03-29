package org.examora.examora.utilisateur.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name ="etudiant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)

public class Etudiant extends Utilisateur{
    // Identifiant unique ex: 2024-INFO-042
    @Column(unique = true, nullable = false)
    private String matricule;

    @Column(nullable = false)
    private String filiere;

    // Ex: "S1", "S2", "S3"...
    @Column(nullable = false)
    private String semestre;

    @Column(nullable = false)
    private Integer anneeEtude;

}
