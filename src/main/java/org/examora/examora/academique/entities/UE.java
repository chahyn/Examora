package org.examora.examora.academique.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="ues")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @OneToMany(mappedBy ="ue",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    private List<Matiere> matieres = new ArrayList<>();

    @OneToOne(mappedBy = "ue",
            cascade = CascadeType.ALL)
    private CoefficientsUE coefficient;

}
