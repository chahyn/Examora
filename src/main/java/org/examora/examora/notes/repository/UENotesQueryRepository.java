package org.examora.examora.notes.repository;

import org.examora.examora.academique.entities.Semestre;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UENotesQueryRepository {

    @PersistenceContext
    private EntityManager em;
    public record LigneUEMatiere(
            Long   ueId,
            String ueNom,
            int    creditsUE,
            Long   matiereId,
            String matiereNom,
            double coefficient,
            boolean hasTp
    ) {}

    public List<LigneUEMatiere> findLignesUEParSemestre(Semestre semestre) {
        String jpql = """
                SELECT ue.id,
                       ue.nom,
                       ue.credits,
                       m.id,
                       m.nom,
                       c.coefficient,
                       m.hasTp
                FROM UE ue
                JOIN ue.coefficients c
                JOIN c.matiere m
                WHERE ue.semestre = :semestre
                ORDER BY ue.id, m.nom
                """;

        List<Object[]> rows = em.createQuery(jpql, Object[].class)
                .setParameter("semestre", semestre)
                .getResultList();

        return rows.stream().map(r -> new LigneUEMatiere(
                ((Number) r[0]).longValue(),
                (String)  r[1],
                ((Number) r[2]).intValue(),
                ((Number) r[3]).longValue(),
                (String)  r[4],
                ((Number) r[5]).doubleValue(),
                (Boolean) r[6]
        )).toList();
    }
    public List<Object[]> findUEsParSemestre(Semestre semestre) {
        return em.createQuery("""
                SELECT ue.id, ue.nom, ue.credits
                FROM UE ue
                WHERE ue.semestre = :semestre
                ORDER BY ue.id
                """, Object[].class)
                .setParameter("semestre", semestre)
                .getResultList();
    }
}
