package org.examora.examora.academique.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "semestre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private Integer numero;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "filiere_id" , nullable = false)
    private Filiere filiere;

    @OneToMany(mappedBy = "semestre",
    cascade = CascadeType.ALL ,
    fetch = FetchType.LAZY)
    private List<UE> ues = new ArrayList<>();



}
