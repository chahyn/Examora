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
        // 1. Check if UE already exists in that semester
        if (ueRepo.existsBySemestreIdAndNom(request.getSemestreId(), request.getNom())) {
            throw new RuntimeException("Cette UE existe déjà dans ce semestre");
        }

        // 2. Validate that the semester exists
        Semestre semestre = semestreRepo.findById(request.getSemestreId())
                .orElseThrow(() -> new RuntimeException("Semestre introuvable"));

        // 3. Build the entity (Correctly assigned to variable 'ue')
        UE ue = UE.builder()
                .nom(request.getNom())
                .code(request.getCode())
                .semestre(semestre)
                .build();

        // 4. Save and convert to DTO
        return toDTO(ueRepo.save(ue));
    }

    // UPDATE
    public UEDTO modifier(Long id, UERequest request) {
        // 1. Find the existing UE
        UE ue = ueRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("UE introuvable"));

        // 2. Validate the new semester exists
        Semestre semestre = semestreRepo.findById(request.getSemestreId())
                .orElseThrow(() -> new RuntimeException("Semestre introuvable"));

        // 3. Update fields
        ue.setNom(request.getNom());
        ue.setCode(request.getCode());
        ue.setSemestre(semestre);

        // 4. Save and convert to DTO
        return toDTO(ueRepo.save(ue));
    }

    // DELETE
    public void supprimer(Long id) {
        if (!ueRepo.existsById(id)) {
            throw new RuntimeException("UE introuvable");
        }
        ueRepo.deleteById(id);
    }

    // TO DTO helper method
    private UEDTO toDTO(UE ue) {
        return new UEDTO(
                ue.getId(),
                ue.getNom(),
                ue.getCode(),
                ue.getSemestre() != null ? ue.getSemestre().getId() : null,
                ue.getSemestre() != null ? ue.getSemestre().getNom() : "N/A",
                ue.getMatieres() != null ? ue.getMatieres().size() : 0

        );
    }
}