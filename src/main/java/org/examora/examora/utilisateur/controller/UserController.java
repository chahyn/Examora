package org.examora.examora.utilisateur.controller;

import lombok.RequiredArgsConstructor;
import org.examora.examora.utilisateur.dto.UtilisateurDTO;
import org.examora.examora.utilisateur.entities.Role;
import org.examora.examora.utilisateur.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> getAll(){
        return ResponseEntity.ok(utilisateurService.getAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getById(id));
    }

    @PatchMapping("/{id}/actif")
    public ResponseEntity<Void> toggleActif(
            @PathVariable Long id) {
        utilisateurService.toggleActif(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/byRole")
    public ResponseEntity<List<UtilisateurDTO>> getByRole(
            @RequestParam Role role) {
        return ResponseEntity.ok(utilisateurService.getByRole(role));
    }

}
