package org.examora.examora.academique.repository;

import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.entities.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere , Long> {

    Optional<Filiere> findByNom(String nom);
    Optional<Filiere> findByCode(String code);
    boolean existsByCode(String code);
    List<Filiere> findByDureeAnnees(Integer duree);

}
