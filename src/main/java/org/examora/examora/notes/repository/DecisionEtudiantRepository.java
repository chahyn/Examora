package org.examora.examora.notes.repository;

import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.notes.entities.DecisionEtudiant;
import org.examora.examora.notes.entities.DecisionType;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DecisionEtudiantRepository extends JpaRepository<DecisionEtudiant, Long> {

    Optional<DecisionEtudiant> findByEtudiantAndSemestreAndAnneeUniversitaire(
            Etudiant etudiant, Semestre semestre, String anneeUniversitaire);

    List<DecisionEtudiant> findBySemestreAndAnneeUniversitaire(
            Semestre semestre, String anneeUniversitaire);

    List<DecisionEtudiant> findByEtudiantAndAnneeUniversitaire(
            Etudiant etudiant, String anneeUniversitaire);

    @Query("""
            SELECT d.decision, COUNT(d)
            FROM DecisionEtudiant d
            WHERE d.semestre = :semestre
              AND d.anneeUniversitaire = :annee
            GROUP BY d.decision
            """)
    List<Object[]> countByDecisionType(@Param("semestre") Semestre semestre,
                                       @Param("annee") String annee);

    @Query("""
            SELECT AVG(d.moyenneSemestre)
            FROM DecisionEtudiant d
            WHERE d.semestre = :semestre
              AND d.anneeUniversitaire = :annee
              AND d.moyenneSemestre IS NOT NULL
            """)
    Double findMoyennePromo(@Param("semestre") Semestre semestre,
                            @Param("annee") String annee);

    List<DecisionEtudiant> findBySemestreAndAnneeUniversitaireAndDecision(
            Semestre semestre, String anneeUniversitaire, DecisionType decision);
}
