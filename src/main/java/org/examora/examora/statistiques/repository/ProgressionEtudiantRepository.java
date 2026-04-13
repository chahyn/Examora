package org.examora.examora.statistiques.repository;

import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.statistiques.entities.ProgressionEtudiant;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionEtudiantRepository extends JpaRepository<ProgressionEtudiant, Long> {

    Optional<ProgressionEtudiant> findByEtudiantAndSemestreAndAnneeUniversitaire(
            Etudiant etudiant, Semestre semestre, String anneeUniversitaire);

    /** Historique complet d'un étudiant, trié chronologiquement */
    @Query("""
            SELECT p FROM ProgressionEtudiant p
            WHERE p.etudiant = :etudiant
            ORDER BY p.semestre.numero ASC
            """)
    List<ProgressionEtudiant> findHistoriqueEtudiant(@Param("etudiant") Etudiant etudiant);

    /** Top N étudiants d'un semestre (pour classement) */
    @Query("""
            SELECT p FROM ProgressionEtudiant p
            WHERE p.semestre = :semestre
              AND p.anneeUniversitaire = :annee
              AND p.moyenneSemestre IS NOT NULL
            ORDER BY p.moyenneSemestre DESC
            """)
    List<ProgressionEtudiant> findClassementSemestre(
            @Param("semestre") Semestre semestre,
            @Param("annee") String annee);

    /** Étudiants en régression (delta négatif significatif) */
    @Query("""
            SELECT p FROM ProgressionEtudiant p
            WHERE p.semestre = :semestre
              AND p.anneeUniversitaire = :annee
              AND p.deltaMoyenne < :seuil
            ORDER BY p.deltaMoyenne ASC
            """)
    List<ProgressionEtudiant> findEtudiantsEnRegression(
            @Param("semestre") Semestre semestre,
            @Param("annee") String annee,
            @Param("seuil") double seuil);
}
