package org.examora.examora.examen.repository;

import org.examora.examora.examen.entities.CoursDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursDocumentRepository extends JpaRepository<CoursDocument, Long> {

    List<CoursDocument> findByProfesseurId(Long professeurId);
    List<CoursDocument> findByMatiereId(Long matiereId);
    List<CoursDocument> findByMatiereIdAndVisibleEtudiantsTrue(Long matiereId);
    Optional<CoursDocument> findByCheminStockage(String cheminStockage);
}