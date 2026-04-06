package org.examora.examora.utilisateur.repository;

import org.examora.examora.utilisateur.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Long> {

    Optional<RefreshToken> findByToken(String refreshToken);
    boolean existsByToken(String refreshToken);
    void deleteByUtilisateurId(Long id);

}
