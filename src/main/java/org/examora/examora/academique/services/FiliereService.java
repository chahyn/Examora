package org.examora.examora.academique.services;

import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.FiliereDTO;
import org.examora.examora.academique.dto.FiliereRequest;
import org.examora.examora.academique.entities.Filiere;
import org.examora.examora.academique.repository.FiliereRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FiliereService {
    private final FiliereRepository filiereRepo;

    //Get all filieres
    public List<FiliereDTO> getAll(){
        return filiereRepo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    //une seule filiere by ID
    public FiliereDTO getById(Long id){
        return filiereRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("filiere introuvable"));

    }

    // creer une nouvelle filiere

    public FiliereDTO create(FiliereRequest request){
        //validation du code
        if(filiereRepo.existsByCode(request.getCode())){
            throw new RuntimeException("filiere deja existe");
        }

        // build entity
        Filiere filiere = new Filiere().builder()
                .nom(request.getNom())
                .code(request.getCode())
                .dureeAnnees(request.getDureeAnnees())
                .build();

        return filiereRepo.save(filiere)
    }
}
