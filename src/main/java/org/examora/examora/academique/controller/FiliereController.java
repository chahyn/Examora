package org.examora.examora.academique.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.FiliereDTO;
import org.examora.examora.academique.dto.FiliereRequest;
import org.examora.examora.academique.services.FiliereService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/filieres")
@RequiredArgsConstructor
public class FiliereController {
    private final FiliereService filiereService;

    @GetMapping
    public ResponseEntity<List<FiliereDTO>> getAll() {
        return ResponseEntity.ok(filiereService.getAll());
    }

    // GET /api/v1/filieres/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FiliereDTO> getById(@PathVariable Long id) {
        // getById returns entity, wrap it via toDTO in service —
        // since getById returns Filiere, we call getAll-style;
        // NOTE: You should add a getByIdDTO method in service for clean API.
        // For now we return 200 with the raw call wrapped:
        return ResponseEntity.ok(filiereService.getAll()
                .stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Filiere introuvable")));
    }

    // POST /api/v1/filieres
    @PostMapping
    public ResponseEntity<FiliereDTO> create(@Valid @RequestBody FiliereRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filiereService.create(request));
    }
}
