package org.examora.examora.examen.repository;

import org.examora.examora.examen.entities.Variante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VarianteRepository extends JpaRepository<Variante, Long> {

    List<Variante> findByExamenId(Long examenId);

    Optional<Variante> findByExamenIdAndCodeVariante(Long examenId, TypeVariante codeVariante);
}
