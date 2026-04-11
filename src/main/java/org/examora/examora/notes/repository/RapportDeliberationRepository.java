package org.examora.examora.notes.repository;

import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.notes.entities.RapportDeliberation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RapportDeliberationRepository extends JpaRepository<RapportDeliberation, Long> {

    Optional<RapportDeliberation> findBySemestreAndAnneeUniversitaire(
            Semestre semestre, String anneeUniversitaire);

    boolean existsBySemestreAndAnneeUniversitaire(Semestre semestre, String anneeUniversitaire);
}
