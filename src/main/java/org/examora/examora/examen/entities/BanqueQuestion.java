package org.examora.examora.examen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "banque_question",
        //pour qu'un banque seul pour chaque matiere et chaque session
        uniqueConstraints = @UniqueConstraint(columnNames = {"matiere_id" , "session_academique"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanqueQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;
    @Column(name = "session_academique", nullable = false)
    private String sessionAcademique;

    @OneToMany(mappedBy = "banqueQuestion", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    private LocalDateTime dateCreation;

    @PrePersist
    public void avantSauvegarde() {
        this.dateCreation = LocalDateTime.now();
    }

}
