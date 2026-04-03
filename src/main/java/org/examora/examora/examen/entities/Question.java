package org.examora.examora.examen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The exam this question belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id", nullable = false)
    private Examen examen;

    //the bank question stored in
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banque_question_id")
    private BanqueQuestion banqueQuestion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeQuestion type;

    // TEXT in DB because question text can be very long
    @Column(columnDefinition = "TEXT", nullable = false)
    private String enonce;

    // MCQ options — all null when type = OUVERTE
    @Column(name = "option_a")
    private String optionA;

    @Column(name = "option_b")
    private String optionB;

    @Column(name = "option_c")
    private String optionC;

    @Column(name = "option_d")
    private String optionD;

    @Column(name = "reponse_correcte")
    private String reponseCorrecte;

    @Column(nullable = false)
    private Double points;

    @Enumerated(EnumType.STRING)
    private NiveauDifficulte difficulte;

    // Has the professor reviewed and approved this question?
    // Claude's output is never published without professor validation.
    @Column(nullable = false)
    private boolean valideeProfesseur = false;

}
