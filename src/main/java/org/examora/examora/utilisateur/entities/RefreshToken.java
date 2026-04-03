package org.examora.examora.utilisateur.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La valeur unique du token envoyée au client
    @Column(unique = true, nullable = false)
    private String token;

    // L'utilisateur propriétaire de ce token
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // Date d'expiration (généralement 7 jours)
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    // Méthode utilitaire pour vérifier si expiré
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}