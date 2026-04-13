package org.examora.examora.statistiques.repository;

import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.statistiques.entities.StatistiquesExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatistiquesExamenRepository extends JpaRepository<StatistiquesExamen, Long> {

    Optional<StatistiquesExamen> findByMatiereAndSemestreAndAnneeUniversitaire(
            Matiere matiere, Semestre semestre, String anneeUniversitaire);

    List<StatistiquesExamen> findBySemestreAndAnneeUniversitaire(
            Semestre semestre, String anneeUniversitaire);

    /** Toutes les stats d'une filière pour un tableau de bord admin */
    @Query("""
            SELECT s FROM StatistiquesExamen s
            WHERE s.semestre.filiere.id = :filiereId
              AND s.anneeUniversitaire = :annee
            ORDER BY s.semestre.numero, s.matiere.nom
            """)
    List<StatistiquesExamen> findByFiliereAndAnnee(
            @Param("filiereId") Long filiereId,
            @Param("annee") String annee);

    /** Matières avec taux de réussite inférieur au seuil (alerte admin) */
    @Query("""
            SELECT s FROM StatistiquesExamen s
            WHERE s.semestre = :semestre
              AND s.anneeUniversitaire = :annee
              AND s.tauxReussite < :seuil
            ORDER BY s.tauxReussite ASC
            """)
    List<StatistiquesExamen> findMatieresCritiques(
            @Param("semestre") Semestre semestre,
            @Param("annee") String annee,
            @Param("seuil") double seuil);
}
