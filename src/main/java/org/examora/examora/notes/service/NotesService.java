package org.examora.examora.notes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.examora.examora.academique.entities.Matiere;
import org.examora.examora.academique.entities.Semestre;
import org.examora.examora.academique.repository.MatiereRepository;
import org.examora.examora.notes.dto.NoteRequest;
import org.examora.examora.notes.dto.SaisieNotesDTO;
import org.examora.examora.notes.dto.SaisieNotesGroupeRequest;
import org.examora.examora.notes.entities.Note;
import org.examora.examora.notes.repository.NoteRepository;
import org.examora.examora.notes.repository.SemestreRepository;
import org.examora.examora.utilisateur.entities.Etudiant;
import org.examora.examora.utilisateur.repository.EtudiantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotesService {

    private final NoteRepository     noteRepository;
    private final MatiereRepository  matiereRepository;
    private final SemestreRepository semestreRepository;
    private final EtudiantRepository etudiantRepository;

    @Transactional
    public SaisieNotesDTO saisirOuMettreAJour(NoteRequest request) {
        Etudiant etudiant = chargerEtudiant(request.getEtudiantId());
        Matiere  matiere  = chargerMatiere(request.getMatiereId());
        Semestre semestre = chargerSemestre(request.getSemestreId());

        validerNotesRequest(request);

        Optional<Note> existante = noteRepository
                .findByEtudiantAndMatiereAndSemestreAndAnneeUniversitaire(
                        etudiant, matiere, semestre, request.getAnneeUniversitaire());

        Note note = existante.orElseGet(() -> Note.builder()
                .etudiant(etudiant)
                .matiere(matiere)
                .semestre(semestre)
                .anneeUniversitaire(request.getAnneeUniversitaire())
                .build());

        note.setHasTp(request.isHasTp());
        note.setNoteDS(request.getNoteDS());
        note.setNoteExamen(request.getNoteExamen());
        note.setNoteTP(request.isHasTp() ? request.getNoteTP() : null);

        Note saved = noteRepository.save(note);
        log.info("[NOTES] Note sauvegardée — étudiant:{} matière:{} fusionnée:{}",
                etudiant.getMatricule(), matiere.getNom(), saved.getNoteFusionnee());

        return SaisieNotesDTO.fromNote(saved);
    }

    @Transactional
    public List<SaisieNotesDTO> saisirNotesGroupe(SaisieNotesGroupeRequest request) {
        List<SaisieNotesDTO> resultats = new ArrayList<>();
        for (NoteRequest nr : request.getNotes()) {
            nr.setMatiereId(request.getMatiereId());
            nr.setSemestreId(request.getSemestreId());
            nr.setAnneeUniversitaire(request.getAnneeUniversitaire());
            resultats.add(saisirOuMettreAJour(nr));
        }
        log.info("[NOTES] Saisie groupe — {} notes pour matiereId:{}",
                resultats.size(), request.getMatiereId());
        return resultats;
    }

    @Transactional
    public int validerNotes(Long matiereId, Long semestreId, String anneeUniversitaire) {
        Matiere  matiere  = chargerMatiere(matiereId);
        Semestre semestre = chargerSemestre(semestreId);

        List<Note> notes = noteRepository
                .findByMatiereAndSemestreAndAnneeUniversitaire(matiere, semestre, anneeUniversitaire);

        notes.forEach(n -> {
            if (!n.estComplete()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Note incomplète pour l'étudiant : " + n.getEtudiant().getMatricule());
            }
            n.setValideeProfesseur(true);
        });

        noteRepository.saveAll(notes);
        log.info("[NOTES] {} notes validées — matiereId:{}", notes.size(), matiereId);
        return notes.size();
    }

    public List<SaisieNotesDTO> getNotesByMatiereSemestre(Long matiereId, Long semestreId,
                                                          String anneeUniversitaire) {
        return noteRepository
                .findByMatiereAndSemestreAndAnneeUniversitaire(
                        chargerMatiere(matiereId),
                        chargerSemestre(semestreId),
                        anneeUniversitaire)
                .stream()
                .map(SaisieNotesDTO::fromNote)
                .toList();
    }

    public List<SaisieNotesDTO> getNotesByEtudiantSemestre(Long etudiantId, Long semestreId,
                                                           String anneeUniversitaire) {
        return noteRepository
                .findByEtudiantAndSemestreAndAnneeUniversitaire(
                        chargerEtudiant(etudiantId),
                        chargerSemestre(semestreId),
                        anneeUniversitaire)
                .stream()
                .map(SaisieNotesDTO::fromNote)
                .toList();
    }

    public Double getTauxReussite(Long matiereId, Long semestreId, String annee) {
        return noteRepository.findTauxReussite(
                chargerMatiere(matiereId), chargerSemestre(semestreId), annee);
    }

    public List<Double> getDistributionNotes(Long matiereId, Long semestreId, String annee) {
        return noteRepository.findDistributionNotes(
                chargerMatiere(matiereId), chargerSemestre(semestreId), annee);
    }


    private Etudiant chargerEtudiant(Long id) {
        return etudiantRepository.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Étudiant introuvable : id=" + id));
    }

    private Matiere chargerMatiere(Long id) {
        return matiereRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Matière introuvable : id=" + id));
    }

    private Semestre chargerSemestre(Long id) {
        return semestreRepository.findById(id.intValue())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Semestre introuvable : id=" + id));
    }

    private void validerNotesRequest(NoteRequest req) {
        if (req.isHasTp() && req.getNoteTP() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La note TP est obligatoire quand hasTp = true");
        }
    }
}
