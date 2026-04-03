package org.examora.examora.examen.dto;

import org.examora.examora.examen.entities.CoursDocument;
import org.examora.examora.examen.entities.Examen;
import org.examora.examora.examen.entities.ExamenConfig;
import org.examora.examora.examen.entities.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExamenMapper {

    public ExamenDto toDto(Examen examen) {
        ExamenDto dto = new ExamenDto();
        dto.setId(examen.getId());
        dto.setStatut(examen.getStatut());
        dto.setDateCreation(examen.getDateCreation());
        dto.setDateDebut(examen.getDateDebut());
        dto.setDateFin(examen.getDateFin());
        dto.setNoteTotale(examen.getNoteTotale());
        dto.setEnCours(examen.estEnCours());
        dto.setTermine(examen.estTermine());

        if (examen.getMatiere() != null) {
            dto.setMatiereId(examen.getMatiere().getId());
            dto.setMatiereNom(examen.getMatiere().getNom());
        }
        if (examen.getProfesseur() != null) {
            dto.setProfesseurId(examen.getProfesseur().getId());
            dto.setProfesseurNom(examen.getProfesseur().getNom());
        }
        if (examen.getCoursSource() != null) {
            dto.setCoursSourceId(examen.getCoursSource().getId());
            dto.setCoursSourceNom(examen.getCoursSource().getNomFichier());
        }
        if (examen.getConfig() != null) {
            dto.setConfig(toConfigDto(examen.getConfig()));
        }
        if (examen.getQuestions() != null) {
            dto.setQuestions(examen.getQuestions().stream()
                    .map(this::toQuestionDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public ExamenConfigDto toConfigDto(ExamenConfig config) {
        ExamenConfigDto dto = new ExamenConfigDto();
        dto.setDureeMinutes(config.getDureeMinutes());
        dto.setBareme(config.getBareme());
        dto.setNbQcm(config.getNbQcm());
        dto.setNbOuverte(config.getNbOuverte());
        dto.setNiveau(config.getNiveau());
        dto.setType(config.getType());
        return dto;
    }

    public ExamenConfig toConfigEntity(ExamenConfigDto dto) {
        ExamenConfig config = new ExamenConfig();
        config.setDureeMinutes(dto.getDureeMinutes());
        config.setBareme(dto.getBareme());
        config.setNbQcm(dto.getNbQcm());
        config.setNbOuverte(dto.getNbOuverte());
        config.setNiveau(dto.getNiveau());
        config.setType(dto.getType());
        return config;
    }

    public QuestionDto toQuestionDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setType(question.getType());
        dto.setEnonce(question.getEnonce());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setReponseCorrecte(question.getReponseCorrecte());
        dto.setPoints(question.getPoints());
        dto.setDifficulte(question.getDifficulte());
        dto.setValideeProfesseur(question.isValideeProfesseur());
        return dto;
    }

    public CoursDocumentDto toCoursDocumentDto(CoursDocument doc) {
        CoursDocumentDto dto = new CoursDocumentDto();
        dto.setId(doc.getId());
        dto.setNomFichier(doc.getNomFichier());
        dto.setCheminStockage(doc.getCheminStockage());
        dto.setTailleFichierOctets(doc.getTailleFichierOctets());
        dto.setDateUpload(doc.getDateUpload());
        dto.setVisibleEtudiants(doc.isVisibleEtudiants());
        dto.setTraiteParIA(doc.getTraiteParIA());
        if (doc.getMatiere() != null) {
            dto.setMatiereId(doc.getMatiere().getId());
            dto.setMatiereNom(doc.getMatiere().getNom());
        }
        if (doc.getProfesseur() != null) {
            dto.setProfesseurId(doc.getProfesseur().getId());
            dto.setProfesseurNom(doc.getProfesseur().getNom());
        }
        return dto;
    }

    public List<ExamenDto> toDtoList(List<Examen> examens) {
        return examens.stream().map(this::toDto).collect(Collectors.toList());
    }

}
