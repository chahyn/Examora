package org.examora.examora.utilisateur.service;

import lombok.RequiredArgsConstructor;
import org.examora.examora.utilisateur.dto.AuthResponse;
import org.examora.examora.utilisateur.dto.LoginRequest;
import org.examora.examora.utilisateur.dto.RegisterRequest;
import org.examora.examora.utilisateur.entities.*;
import org.examora.examora.utilisateur.repository.RefreshTokenRepository;
import org.examora.examora.utilisateur.repository.UtilisateurRepository;
import org.examora.examora.security.JwtUtils;                          // ← add this import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.examora.examora.utilisateur.entities.Role.ETUDIANT;
import static org.examora.examora.utilisateur.entities.Role.PROFESSEUR;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        Utilisateur user = utilisateurRepo
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email introuvable"));

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // 3. Generate JWT — pass email string instead of user object
        String accessToken = jwtUtils.generateToken(user.getEmail());  // ← fixed

        // 4. Generate and save refresh token
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .utilisateur(user)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepo.save(refreshToken);

        // 5. Return response
        return new AuthResponse(accessToken, refreshTokenValue, user.getRole());
    }

    public void register(RegisterRequest request) {
        // 1. Check email not already used
        if (utilisateurRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email deja utilisé");  // ← added semicolon
        }

        // 2. Build entity based on role
        Utilisateur newUser = switch (request.getRole()) {
            case PROFESSEUR -> Professeur.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(Role.PROFESSEUR)
                    .build();
            case ETUDIANT -> Etudiant.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(ETUDIANT)
                    .build();
            default -> Administration.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ADMINISTRATEUR)
                    .build();
        };

        // 3. Save to DB
        utilisateurRepo.save(newUser);
    }

    public void logout(Long utilisateurId) {
        refreshTokenRepo.deleteByUtilisateurId(utilisateurId);
    }
}