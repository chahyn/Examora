package org.examora.examora.statistiques.repository;

import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.statistiques.entities.AlertePrecoce;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertePrecoceRepository extends JpaRepository<AlertePrecoce, Long> {

    List<AlertePrecoce> findByEtudiantAndSemestreAndAnneeUniversitaire(
            Etudiant etudiant, Semestre semestre, String anneeUniversitaire);

    List<AlertePrecoce> findBySemestreAndAnneeUniversitaireAndTraiteeFalse(
            Semestre semestre, String anneeUniversitaire);

    /** Toutes les alertes non traitées, niveau ELEVE en premier */
    @Query("""
            SELECT a FROM AlertePrecoce a
            WHERE a.semestre = :semestre
              AND a.anneeUniversitaire = :annee
              AND a.traitee = false
            ORDER BY
              CASE a.niveauRisque
                WHEN 'ELEVE'  THEN 1
                WHEN 'MOYEN'  THEN 2
                ELSE               3
              END,
              a.moyenneDsActuelle ASC NULLS LAST
            """)
    List<AlertePrecoce> findAlertesActives(
            @Param("semestre") Semestre semestre,
            @Param("annee") String annee);

    boolean existsByEtudiantAndSemestreAndAnneeUniversitaireAndTraiteeFalse(
            Etudiant etudiant, Semestre semestre, String anneeUniversitaire);
}
