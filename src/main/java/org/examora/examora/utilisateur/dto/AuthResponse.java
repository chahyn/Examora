package org.examora.examora.utilisateur.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.examora.examora.utilisateur.entities.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String acessToken;
    private String refreshToken;
    private Role role;
}
