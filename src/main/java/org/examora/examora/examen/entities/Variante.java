package org.examora.examora.examen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "variante")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Variante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "A", "B", or "C"
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeVariante codeVariante;

    // The exam this variant belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id", nullable = false)
    private Examen examen;

    // The questions in shuffled order.
    @ManyToMany
    @JoinTable(
            name = "variante_questions",
            joinColumns = @JoinColumn(name = "variante_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    // @OrderColumn persists the shuffled order as a "position" column
    // in the join table. Without this, the order is not guaranteed.
    @OrderColumn(name = "position")
    private List<Question> questions = new ArrayList<>();
}
