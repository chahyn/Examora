package org.examora.examora.utilisateur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    //valid Email
    @NotBlank
    @Email
    private String email;

    //password can't be empty or only spaces
    @NotBlank
    @Size(min=8)
    private String password;

}
