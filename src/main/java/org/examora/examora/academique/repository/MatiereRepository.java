package org.examora.examora.academique.repository;

import org.examora.examora.academique.entities.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere , Long> {
    Optional<Matiere> findByUeId(Long ueId);
    List<Matiere> findByProfesseurId(Long id);
    Optional<Matiere> findByCode(String code);
    boolean existsByCode (String code);
    boolean existsByUeIdAndNom(Long ueId, String nom);
}
