package org.examora.examora.examen.repository;

import org.examora.examora.examen.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question , Long> {
    List<Question> findByExamenId(Long examenId);
    List<Question > findByExamenIdAndValideeProfesseurTrue(Long examenId);
    List<Question> findByBanqueQuestionId(Long banqueId);

}
