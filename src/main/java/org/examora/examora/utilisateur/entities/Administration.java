package org.examora.examora.utilisateur.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="administration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder

public class Administration extends Utilisateur {

    @Column
    private String departement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauAcces niveauAcces = NiveauAcces.ADMIN;

    public enum NiveauAcces {
        SUPER_ADMIN, ADMIN
    }
}
