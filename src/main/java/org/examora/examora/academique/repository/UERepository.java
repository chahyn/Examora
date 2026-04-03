package org.examora.examora.academique.repository;

import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.entities.UE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UERepository extends JpaRepository<UE , Long> {
    List<UE> findBySemestreId(Long id);
    Optional<UE> findByCode(String code);
    boolean existsByCode(String code);
    List<UE> findBySemestreIDOrderByNomAsc(Long id);
boolean existsBySemestreIdAndNom(Long SemestreId, String nom);
}
