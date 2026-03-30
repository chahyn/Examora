package org.examora.examora.examen.service;

import lombok.RequiredArgsConstructor;
import org.examora.examora.examen.entities.*;
import org.examora.examora.examen.repository.ExamenRepository;
import org.examora.examora.utilisateur.entities.Professeur;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamenService {
    private final ExamenRepository examenRepo;

    //creer un examen
    // this function called by Claude after generating the examen
    public Examen creer (Matiere maatiere,
                         Professeur professeur,
                         ExamenConfig config,
                         CoursDocument coursSource){
        Examen examen = Examen.builder()
                .matiere(matiere)
                .professeur(professeur)
                .config(config)
                .coursSource(coursSource)
                .statut(StatutExamen.EN_ATTENTE)
                .build();
        return examenRepo.save(examen);
    }

    //validation du prof pour l'examen
    public Examen valider(Long examenId , Professeur caller){
        Examen examen = examenRepo.findById(examenId)
                .orElseThrow(()-> new RuntimeException("Examen introuvable"));

        if (!examen.getProfesseur().getId().equals(caller.getId())){
            throw new RuntimeException(("acces refusé"));
        }

        //prof doit valider touts les question
        boolean toutesValidees = examen.getQuestions().stream()
                .allMatch(Question::isValideeProfesseur);
        if (!toutesValidees) {
            throw new RuntimeException("vous devez valider toutes les questions avant de valider l'examen");

        }

        examen.setStatut(StatutExamen.VALIDE);
        return examenRepo.save(examen);
    }

    //Planifier l'examen
    public  Examen planifier (Long examenId, LocalDateTime dateDebut){
        Examen examen = examenRepo.findById(examenId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if(examen.getStatut()!=StatutExamen.VALIDE){
            throw new RuntimeException("l'examen doit etre validé avant la planification");

        }

        LocalDateTime datefin = dateDebut.plusMinutes(examen.getConfig().getDureeMinutes());
        examen.setDateDebut(dateDebut);
        examen.setDateFin(datefin);
        examen.setStatut(StatutExamen.PLANIFIE);

        return examenRepo.save(examen);
    }

    //apres l'examen
    public Examen terminer (Long examenId) {
        Examen examen= examenRepo.
                findById(examenId).orElseThrow(() -> new RuntimeException("Not found"));
        examen.setStatut(StatutExamen.TERMINE);
        return examenRepo.save(examen);
    }

    //Validation des questions
    public Question validerQuestion(Long examenId,
                                    Long questionId,
                                    Professeur caller){
        Examen examen = examenRepo.findById(examenId)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (!examen.getProfesseur().getId().equals(caller.getId())){
            throw new RuntimeException("accès refusé");
        }
        Question question = examen.getQuestions().stream()
                .filter(q->q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Question introuvable"));

        question.setValideeProfesseur(true);
        return question;

    }
    public Examen findById(Long id) {
        return examenRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen introuvable"));
    }

    public List<Examen> findByProfesseur(Long professeurId) {
        return examenRepo.findByProfesseurId(professeurId);
    }

    public List<Examen> findByProfesseurAndStatut(Long professeurId,
                                                  StatutExamen statut) {
        return examenRepo.findByProfesseurIdAndStatut(professeurId, statut);
    }
    public List<Examen> findEnCoursForEtudiant(Long matiereId) {
        return examenRepo
                .findByMatiereIdAndStatut(
                        matiereId,
                        List.of(StatutExamen.PLANIFIE)
                )
                .stream()
                .filter(Examen::estEnCours)
                .collect(Collectors.toList());
    }
    public List<Examen> findAccessiblesByEtudiant(Long matiereId) {
        return examenRepo.findByMatiereIdAndStatut(
                matiereId,
                List.of(StatutExamen.PLANIFIE, StatutExamen.TERMINE)
        );
    }


}
