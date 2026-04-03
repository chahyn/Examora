package org.examora.examora.utilisateur.repository;

import org.examora.examora.utilisateur.entities.Role;
import org.examora.examora.utilisateur.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    //find user by their email - at login
    Optional<Utilisateur> findByEmail(String email);

    // check if the email exists
    boolean existsByEmail(String email);

    //Get actif users
    List<Utilisateur> findByActifTrue();

    //get users with specific role
    List<Utilisateur> findByRole(Role role);

    //remove user
    void deleteById(Long id);


}
