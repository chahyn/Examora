package org.examora.examora.academique.services;

import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.MatiereDTO;
import org.examora.examora.academique.dto.MatiereRequest;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.entities.UE;
import org.examora.examora.academique.repository.MatiereRepository;
import org.examora.examora.academique.repository.UERepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatiereService {

    private final MatiereRepository matiereRepo;
    private final UERepository ueRepo;

    // GET ALL
    public List<MatiereDTO> getAll() {
        return matiereRepo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // GET BY ID
    public MatiereDTO getById(Long id) {
        Matiere m = matiereRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière introuvable"));
        return toDTO(m);
    }

    // GET BY UE
    public List<MatiereDTO> getByUE(Long ueId) {
        return matiereRepo.findById(ueId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // CREATE
    public MatiereDTO creer(MatiereRequest request) {
        if (matiereRepo.existsByUeIdAndNom(request.getUeId(), request.getNom())) {
            throw new RuntimeException("Cette matière existe déjà dans cette UE");
        }
        UE ue = ueRepo.findById(request.getUeId())
                .orElseThrow(() -> new RuntimeException("UE introuvable"));
        Matiere matiere = Matiere.builder()
                .nom(request.getNom())
                .code(request.getCode())
                .credits(request.getCredits())
                .ue(ue)
                .build();
        return toDTO(matiereRepo.save(matiere));
    }

    // UPDATE
    public MatiereDTO modifier(Long id, MatiereRequest request) {
        Matiere matiere = matiereRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière introuvable"));
        UE ue = ueRepo.findById(request.getUeId())
                .orElseThrow(() -> new RuntimeException("UE introuvable"));
        matiere.setNom(request.getNom());
        matiere.setCode(request.getCode());
        matiere.setCredits(request.getCredits());
        matiere.setUe(ue);
        return toDTO(matiereRepo.save(matiere));
    }

    // DELETE
    public void supprimer(Long id) {
        if (!matiereRepo.existsById(id)) {
            throw new RuntimeException("Matière introuvable");
        }
        matiereRepo.deleteById(id);
    }

    // TO DTO
    private MatiereDTO toDTO(Matiere m) {
        return new MatiereDTO(
                m.getId(),
                m.getNom(),
                m.getCode(),
                m.getCredits(),
                m.getUe().getId(),
                m.getUe().getNom(),
                m.getProfesseur().getId(),
                m.getProfesseur().getNom()
        );
    }
}