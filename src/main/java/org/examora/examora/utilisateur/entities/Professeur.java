package org.examora.examora.utilisateur.entities;



import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.examora.examora.academique.entities.Matiere;
import java.util.*;

@Entity
@Table(name = "professeurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Professeur extends Utilisateur {

    @Column
    private String specialite;

    @Column
    private String grade; // ex: Maître assistant, Professeur

    // Heures max de surveillance autorisées par semaine
    @Column
    private Integer maxHeuresSurveillance = 4;

    // Un professeur peut enseigner plusieurs matières
    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Matiere> matieres = new ArrayList<>();
}
