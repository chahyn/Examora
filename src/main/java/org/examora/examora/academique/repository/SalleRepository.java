package org.examora.examora.academique.repository;

import org.examora.examora.academique.entities.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {
    List<Salle> findByDisponibleTrue();
    List<Salle> findByCapaciteGreaterThanEqual(Integer min);
    List<Salle> findByBatiment(String batiment);
}
