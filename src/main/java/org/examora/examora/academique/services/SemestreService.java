package org.examora.examora.academique.services;

import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.SemestreDTO;
import org.examora.examora.academique.dto.SemestreRequest;
import org.examora.examora.academique.entities.Filiere;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.academique.repository.FiliereRepository;
import org.examora.examora.academique.repository.SemestreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemestreService {
    private final SemestreRepository semestreRepo;
    private final FiliereRepository filiereRepo;

    //get All Semestre
    public List<SemestreDTO> getAll(){
        return semestreRepo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // seule semestre
    public SemestreDTO getSemestreById(Long id){
        Semestre s = semestreRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestre introuvable"));
        return toDTO(s);
    }
    // creer un nouveau semestre
    public SemestreDTO creerSemestre(SemestreRequest request){
        if(semestreRepo.existsByFiliereIdAndNom(request.getFiliereId() , request.getNom())){
            throw  new RuntimeException("Semestre existe deja");
        }
        Filiere filiere = filiereRepo.findById(request.getFiliereId())
                .orElseThrow(() -> new RuntimeException("Filiere introuvable"));
        Semestre semestre =  Semestre.builder()
                .nom(request.getNom())
                .numero(request.getNumero())
                .filiere(filiere)
                .build();
        return toDTO(semestreRepo.save(semestre));
    }
    //change to DTO
    private SemestreDTO toDTO(Semestre s){
        return new SemestreDTO(
                s.getId(),
                s.getNom(),
                s.getNumero(),
                s.getFiliere().getId(),
                s.getFiliere().getNom());
    }

}
