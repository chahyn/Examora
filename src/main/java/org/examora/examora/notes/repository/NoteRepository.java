package org.examora.examora.notes.repository;

import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.notes.entities.Note;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByEtudiantAndMatiereAndSemestreAndAnneeUniversitaire(
            Etudiant etudiant, Matiere matiere, Semestre semestre, String anneeUniversitaire);
    List<Note> findByEtudiantAndSemestreAndAnneeUniversitaire(
            Etudiant etudiant, Semestre semestre, String anneeUniversitaire);

    List<Note> findByEtudiantAndAnneeUniversitaire(Etudiant etudiant, String anneeUniversitaire);
    List<Note> findByMatiereAndSemestreAndAnneeUniversitaire(
            Matiere matiere, Semestre semestre, String anneeUniversitaire);
    List<Note> findByMatiereAndSemestreAndAnneeUniversitaireAndValideeProfesseurTrue(
            Matiere matiere, Semestre semestre, String anneeUniversitaire);
    @Query("""
            SELECT COUNT(n) * 1.0 / NULLIF(
                (SELECT COUNT(n2) FROM Note n2
                 WHERE n2.matiere = :matiere
                   AND n2.semestre = :semestre
                   AND n2.anneeUniversitaire = :annee
                   AND n2.valideeProfesseur = true), 0)
            FROM Note n
            WHERE n.matiere = :matiere
              AND n.semestre = :semestre
              AND n.anneeUniversitaire = :annee
              AND n.noteFusionnee >= 10
              AND n.valideeProfesseur = true
            """)
    Double findTauxReussite(@Param("matiere") Matiere matiere,
                            @Param("semestre") Semestre semestre,
                            @Param("annee") String annee);
    @Query("""
            SELECT n.noteFusionnee
            FROM Note n
            WHERE n.matiere = :matiere
              AND n.semestre = :semestre
              AND n.anneeUniversitaire = :annee
              AND n.valideeProfesseur = true
              AND n.noteFusionnee IS NOT NULL
            """)
    List<Double> findDistributionNotes(@Param("matiere") Matiere matiere,
                                       @Param("semestre") Semestre semestre,
                                       @Param("annee") String annee);
    @Query("""
            SELECT n.etudiant
            FROM Note n
            WHERE n.semestre = :semestre
              AND n.anneeUniversitaire = :annee
              AND n.noteDS IS NOT NULL
              AND n.noteDS < :seuil
            """)
    List<Etudiant> findEtudiantsNotesDSInsuffisantes(@Param("semestre") Semestre semestre,
                                                     @Param("annee") String annee,
                                                     @Param("seuil") double seuil);

    @Query("""
            SELECT COUNT(n)
            FROM Note n
            WHERE n.matiere = :matiere
              AND n.semestre = :semestre
              AND n.anneeUniversitaire = :annee
              AND n.noteFusionnee IS NULL
            """)
    long countNotesSansValeur(@Param("matiere") Matiere matiere,
                              @Param("semestre") Semestre semestre,
                              @Param("annee") String annee);

    @Query("""
            SELECT COUNT(n) = 0
            FROM Note n
            WHERE n.semestre = :semestre
              AND n.anneeUniversitaire = :annee
              AND n.valideeProfesseur = false
            """)
    boolean sontToutesValidees(@Param("semestre") Semestre semestre,
                               @Param("annee") String annee);
}
