package org.examora.examora.utilisateur.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examora.examora.utilisateur.dto.AuthResponse;
import org.examora.examora.utilisateur.dto.LoginRequest;
import org.examora.examora.utilisateur.dto.RegisterRequest;
import org.examora.examora.utilisateur.dto.UtilisateurDTO;
import org.examora.examora.utilisateur.entities.Role;
import org.examora.examora.utilisateur.service.AuthService;
import org.examora.examora.utilisateur.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UtilisateurService utilisateurService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<void> registre(
            @Valid @RequestBody RegisterRequest request){
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @DeleteMapping("/logout/{id}")
    public ResponseEntity<void> logout(@PathVariable Long id){
        authService.logout(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> getAll(){
        return ResponseEntity.ok(utilisateurService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getById(
            @PathVariable Long id){
        return ResponseEntity.ok(utilisateurService.getById(id));
    }
    @PatchMapping("/{id}/actif")
    public ResponseEntity<void> toggleActif(@PathVariable Long id){
        utilisateurService.toggleActif(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/byRole")
    public ResponseEntity<List<UtilisateurDTO>> getByRole(
            @RequestParam Role role) {
        return ResponseEntity.ok(utilisateurService.getByRole(role));
    }
}
