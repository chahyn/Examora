package org.examora.examora.utilisateur.repository;

import org.examora.examora.utilisateur.entities.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesseurRepository extends JpaRepository<Professeur,Integer> {

    Optional<Professeur> findBySpecialite(String specialite);
    List<Professeur> findByActifTrue();

    @Override
    void deleteById(Integer integer);

    List<Professeur> findByGrade(String grade);

}
