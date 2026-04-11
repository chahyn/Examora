package org.examora.examora.notes.repository;

import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class EtudiantNotesQueryRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Etudiant> findEtudiantsAvecNotes(Semestre semestre, String anneeUniversitaire) {
        return em.createQuery("""
                SELECT DISTINCT n.etudiant
                FROM Note n
                WHERE n.semestre = :semestre
                  AND n.anneeUniversitaire = :annee
                """, Etudiant.class)
                .setParameter("semestre", semestre)
                .setParameter("annee", anneeUniversitaire)
                .getResultList();
    }
}
