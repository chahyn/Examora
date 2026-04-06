package org.examora.examora.academique.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.SemestreDTO;
import org.examora.examora.academique.dto.SemestreRequest;
import org.examora.examora.academique.services.SemestreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/semestres")
@RequiredArgsConstructor
public class SemestreController {
    private final SemestreService semestreService;

    @GetMapping
    public ResponseEntity<List<SemestreDTO>> getAll() {
        return ResponseEntity.ok(semestreService.getAll());
    }

    // GET /api/v1/semestres/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SemestreDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(semestreService.getSemestreById(id));
    }

    // GET /api/v1/semestres/filiere/{filiereId}
    // Useful to list all semestres of a given filiere
    @GetMapping("/filiere/{filiereId}")
    public ResponseEntity<List<SemestreDTO>> getByFiliere(@PathVariable Long filiereId) {
        List<SemestreDTO> result = semestreService.getAll()
                .stream()
                .filter(s -> s.getFiliereId().equals(filiereId))
                .toList();
        return ResponseEntity.ok(result);
    }

    // POST /api/v1/semestres
    @PostMapping
    public ResponseEntity<SemestreDTO> create(@Valid @RequestBody SemestreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(semestreService.creerSemestre(request));
    }
}
