package org.examora.examora.academique.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="filieres")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Integer dureeAnnees;

    @OneToMany(mappedBy = "filiere",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY)
    private List<Semestre> semestres= new ArrayList<>();

}
