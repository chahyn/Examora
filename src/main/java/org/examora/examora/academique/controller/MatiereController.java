package org.examora.examora.academique.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.MatiereDTO;
import org.examora.examora.academique.dto.MatiereRequest;
import org.examora.examora.academique.repository.MatiereRepository;
import org.examora.examora.academique.services.MatiereService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/matieres")
@RequiredArgsConstructor
public class MatiereController {
    private final MatiereRepository matiereRepo;
    private final MatiereService matiereService;
    @GetMapping
    public ResponseEntity<List<MatiereDTO>> getAll() {
        return ResponseEntity.ok(matiereService.getAll());
    }

    // GET /api/v1/matieres/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MatiereDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matiereService.getById(id));
    }

    // GET /api/v1/matieres/ue/{ueId}
    @GetMapping("/ue/{ueId}")
    public ResponseEntity<List<MatiereDTO>> getByUE(@PathVariable Long ueId) {
        return ResponseEntity.ok(matiereService.getByUE(ueId));
    }

    // POST /api/v1/matieres
    @PostMapping
    public ResponseEntity<MatiereDTO> create(@Valid @RequestBody MatiereRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matiereService.creer(request));
    }

    // PUT /api/v1/matieres/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MatiereDTO> update(@PathVariable Long id, @Valid @RequestBody MatiereRequest request) {
        return ResponseEntity.ok(matiereService.modifier(id, request));
    }

    // DELETE /api/v1/matieres/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matiereService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
