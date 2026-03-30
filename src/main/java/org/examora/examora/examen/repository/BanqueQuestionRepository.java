package org.examora.examora.examen.repository;

import org.examora.examora.examen.entities.BanqueQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BanqueQuestionRepository extends JpaRepository<BanqueQuestion, Long> {

    Optional<BanqueQuestion> findByMatiereIdAndSessionAcademique(Long matiereId, String sessionAcademique);

    List<BanqueQuestion> findByMatiereId(Long matiereId);
}