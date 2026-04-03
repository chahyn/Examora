package org.examora.examora.academique.services;


import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.UEDTO;
import org.examora.examora.academique.dto.UERequest;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.academique.entities.UE;
import org.examora.examora.academique.repository.SemestreRepository;
import org.examora.examora.academique.repository.UERepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UEService {

    private final UERepository ueRepo;
    private final SemestreRepository semestreRepo;

    // GET ALL
    public List<UEDTO> getAll() {
        return ueRepo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // GET BY ID
    public UEDTO getById(Long id) {
        UE ue = ueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("UE introuvable"));
        return toDTO(ue);
    }

    // GET BY SEMESTRE
    public List<UEDTO> getBySemestre(Long semestreId) {
        return ueRepo.findBySemestreId(semestreId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // CREATE
    public UEDTO creer(UERequest request) {
        if (ueRepo.existsBySemestreIdAndNom(request.getSemestreId(), request.getNom())) {
            throw new RuntimeException("Cette UE existe déjà dans ce semestre");
        }
        Semestre semestre = semestreRepo.findById(request.getSemestreId())
                .orElseThrow(() -> new RuntimeException("Semestre introuvable"));
        UE ue = UE.builder()
                .nom(request.getNom())
                .code(request.getCode())
                .coefficient(request.getCoefficient())
                .semestre(semestre)
                .build();
        return toDTO(ueRepo.save(ue));
    }

    // UPDATE
    public UEDTO modifier(Long id, UERequest request) {
        UE ue = ueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("UE introuvable"));
        Semestre semestre = semestreRepo.findById(request.getSemestreId())
                .orElseThrow(() -> new RuntimeException("Semestre introuvable"));
        ue.setNom(request.getNom());
        ue.setCode(request.getCode());
        ue.setCoefficient(request.getCoefficient());
        ue.setSemestre(semestre);
        return toDTO(ueRepo.save(ue));
    }

    // DELETE
    public void supprimer(Long id) {
        if (!ueRepo.existsById(id)) {
            throw new RuntimeException("UE introuvable");
        }
        ueRepo.deleteById(id);
    }

    // TO DTO
    private UEDTO toDTO(UE ue) {
        return new UEDTO(
                ue.getId(),
                ue.getNom(),
                ue.getCode(),
                ue.getCoefficient(),
                ue.getSemestre().getId(),
                ue.getSemestre().getNom(),
                ue.getMatieres() != null ? ue.getMatieres().size() : 0
        );
    }
}