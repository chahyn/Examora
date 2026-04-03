package org.examora.examora.examen.repository;

import org.examora.examora.examen.entities.Examen;
import org.examora.examora.examen.entities.StatutExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen , Long> {

    List<Examen> findByProfesseurId(Long professeurId);
    List<Examen> findByProfesseurIdAndStatut(Long professeurId, StatutExamen statutExam);
    List<Examen> findByStatut(StatutExamen statutExamen);
    List<Examen> findByMatiere(Long matiereId);
    List<Examen>findByMatiereIdAndStatut(Long matiereId , List<StatutExamen> statuts);
    Optional<Examen> findById(Long examenId);

}
