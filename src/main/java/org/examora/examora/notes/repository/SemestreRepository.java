package org.examora.examora.notes.repository;

import org.examora.examora.academique.entities.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Integer> {

}
