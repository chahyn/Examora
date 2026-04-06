package org.examora.examora.utilisateur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.examora.examora.utilisateur.entities.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min=8)
    private String password;
    @NotBlank
    @Size(min=8)
    private String confirmPassword;
    @NotBlank
    @NotNull
    private Role role;

}
