package org.examora.examora.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.examora.examora.utilisateur.entities.Professeur;
import org.examora.examora.utilisateur.entities.Role;
import org.examora.examora.utilisateur.entities.Utilisateur;
import org.examora.examora.utilisateur.repository.UtilisateurRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {
    private final JwtUtils jwtUtils;
    private final UtilisateurRepository utilisateurRepo;

    public Utilisateur getCurrentUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant");
        }
        String token = header.substring(7);
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("Token invalide ou expiré");
        }
        String email = jwtUtils.getUsernameFromToken(token);
        return utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public Professeur getCurrentProfesseur(HttpServletRequest request) {
        Utilisateur user = getCurrentUser(request);
        if (!(user instanceof Professeur) || user.getRole() != Role.PROFESSEUR) {
            throw new RuntimeException("Accès réservé aux professeurs");
        }
        return (Professeur) user;
    }

    public Etudiant getCurrentEtudiant(HttpServletRequest request) {
        Utilisateur user = getCurrentUser(request);
        if (!(user instanceof Etudiant) || user.getRole() != Role.ETUDIANT) {
            throw new RuntimeException("Accès réservé aux étudiants");
        }
        return (Etudiant) user;
    }
}
