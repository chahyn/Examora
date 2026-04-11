package org.examora.examora.notes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examora.examora.notes.dto.NoteRequest;
import org.examora.examora.notes.dto.ReleveNotesDTO;
import org.examora.examora.notes.dto.SaisieNotesDTO;
import org.examora.examora.notes.dto.SaisieNotesGroupeRequest;
import org.examora.examora.notes.service.NotesService;
import org.examora.examora.notes.service.ReleveNotesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class NotesController {

    private final NotesService       notesService;
    private final ReleveNotesService releveNotesService;

    @PostMapping("/api/prof/notes")
    @PreAuthorize("hasRole('PROFESSEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<SaisieNotesDTO> saisirNote(
            @Valid @RequestBody NoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notesService.saisirOuMettreAJour(request));
    }

    @PostMapping("/api/prof/notes/groupe")
    @PreAuthorize("hasRole('PROFESSEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<SaisieNotesDTO>> saisirNotesGroupe(
            @Valid @RequestBody SaisieNotesGroupeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notesService.saisirNotesGroupe(request));
    }

    @PutMapping("/api/prof/notes/valider")
    @PreAuthorize("hasRole('PROFESSEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, Object>> validerNotes(
            @RequestParam Long matiereId,
            @RequestParam Long semestreId,
            @RequestParam String anneeUniversitaire) {
        int count = notesService.validerNotes(matiereId, semestreId, anneeUniversitaire);
        return ResponseEntity.ok(Map.of(
                "message", "Notes validées avec succès",
                "nbNotesValidees", count));
    }

    @GetMapping("/api/prof/notes")
    @PreAuthorize("hasRole('PROFESSEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<SaisieNotesDTO>> getNotesByMatiere(
            @RequestParam Long matiereId,
            @RequestParam Long semestreId,
            @RequestParam String anneeUniversitaire) {
        return ResponseEntity.ok(
                notesService.getNotesByMatiereSemestre(matiereId, semestreId, anneeUniversitaire));
    }

    @GetMapping("/api/prof/notes/statistiques")
    @PreAuthorize("hasRole('PROFESSEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Map<String, Object>> getStatistiques(
            @RequestParam Long matiereId,
            @RequestParam Long semestreId,
            @RequestParam String anneeUniversitaire) {
        return ResponseEntity.ok(Map.of(
                "tauxReussite",
                notesService.getTauxReussite(matiereId, semestreId, anneeUniversitaire),
                "distribution",
                notesService.getDistributionNotes(matiereId, semestreId, anneeUniversitaire)));
    }

    @GetMapping("/api/prof/releve/{etudiantId}")
    @PreAuthorize("hasRole('PROFESSEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<ReleveNotesDTO> getReleveEtudiant(
            @PathVariable Long etudiantId,
            @RequestParam Long semestreId,
            @RequestParam String anneeUniversitaire) {
        return ResponseEntity.ok(
                releveNotesService.genererReleve(etudiantId, semestreId, anneeUniversitaire));
    }


    @GetMapping("/api/etudiant/notes")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<SaisieNotesDTO>> getMesNotes(
            @RequestParam Long etudiantId,
            @RequestParam Long semestreId,
            @RequestParam String anneeUniversitaire) {
        return ResponseEntity.ok(
                notesService.getNotesByEtudiantSemestre(etudiantId, semestreId, anneeUniversitaire));
    }

    @GetMapping("/api/etudiant/releve")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<ReleveNotesDTO> getMonReleve(
            @RequestParam Long etudiantId,
            @RequestParam Long semestreId,
            @RequestParam String anneeUniversitaire) {
        return ResponseEntity.ok(
                releveNotesService.genererReleve(etudiantId, semestreId, anneeUniversitaire));
    }
}
