package org.examora.examora.academique.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "salles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;//S1 S2 ...

    @Column(nullable = false)
    private Integer capacite;

    @Column(nullable = false)
    private boolean disponible = true;

    @Column
    private String batiment;// batiment A - Batiment annexe ...
}
