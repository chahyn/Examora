package org.examora.examora.utilisateur.service;

import lombok.RequiredArgsConstructor;
import org.examora.examora.utilisateur.dto.UtilisateurDTO;
import org.examora.examora.utilisateur.entities.Role;
import org.examora.examora.utilisateur.entities.Utilisateur;
import org.examora.examora.utilisateur.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepo;

    private UtilisateurDTO toDTO(Utilisateur u){
        return new UtilisateurDTO(
                u.getId(),
                u.getNom(),
                u.getPrenom(),
                u.getEmail(),
                u.getRole(),
                u.isActif()
        );
    }
    public List<UtilisateurDTO> getAll(){
        return utilisateurRepo.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UtilisateurDTO getById(Long id){
        Utilisateur user = utilisateurRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("utilisateur introuvable"));
        return new UtilisateurDTO();
    }

    public UtilisateurDTO getByEmail(String email){
        Utilisateur user = utilisateurRepo.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("utilisateur introuvable"));
        return new UtilisateurDTO();
    }

    public void toggleActif(Long id){
         Utilisateur user = utilisateurRepo
                 .findById(id)
                 .orElseThrow(()-> new RuntimeException("introuvable"));
         user.setActif(!user.isActif());
         utilisateurRepo.save(user);

    }

    public List<UtilisateurDTO> getByRole(Role role){
        return utilisateurRepo.findByRole(role)
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
