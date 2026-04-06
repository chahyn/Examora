package org.examora.examora.academique.repository;

import org.examora.examora.academique.entities.CoefficientsUE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoefficientUERepository extends JpaRepository<CoefficientsUE , Long> {
    Optional<CoefficientsUE> findByUeId(Long ueId);
    boolean existsByUeId(Long ueId);
}
