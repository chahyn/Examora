package org.examora.examora.academique.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coefficients_ue")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoefficientsUE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double coefficient;

    @OneToOne
    @JoinColumn(name = "ue_id", nullable = false)
    private UE ue;

}
