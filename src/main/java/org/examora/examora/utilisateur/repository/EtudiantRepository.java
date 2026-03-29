package org.examora.examora.utilisateur.repository;

import org.examora.examora.utilisateur.entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {

    Optional<Etudiant> findByMatricule(String matricule);
    boolean existsByMatricule(String matricule);
    List<Etudiant> findByFiliere(String filiere);
    List<Etudiant> findBySemestre(String semestre);
    List<Etudiant> findByAnneeEtude(Integer annee);
    void deleteByMatricule(String matricule);

}
