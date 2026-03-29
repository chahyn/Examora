package org.examora.examora.utilisateur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.examora.examora.utilisateur.entities.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    Role role;
    boolean actif;
}
