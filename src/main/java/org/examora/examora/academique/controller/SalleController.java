package org.examora.examora.academique.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.SalleDTO;
import org.examora.examora.academique.dto.SalleRequest;
import org.examora.examora.academique.entities.Salle;
import org.examora.examora.academique.repository.SalleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/salles")
@RequiredArgsConstructor
public class SalleController {
    private final SalleRepository salleRepo;
    @GetMapping
    public ResponseEntity<List<SalleDTO>> getAll() {
        return ResponseEntity.ok(salleRepo.findAll().stream().map(this::toDTO).toList());
    }

    // GET /api/v1/salles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SalleDTO> getById(@PathVariable Long id) {
        Salle salle = salleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle introuvable"));
        return ResponseEntity.ok(toDTO(salle));
    }

    // GET /api/v1/salles/disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<SalleDTO>> getDisponibles() {
        return ResponseEntity.ok(salleRepo.findByDisponibleTrue().stream().map(this::toDTO).toList());
    }

    // GET /api/v1/salles/capacite?min=30
    @GetMapping("/capacite")
    public ResponseEntity<List<SalleDTO>> getByCapaciteMin(@RequestParam Integer min) {
        return ResponseEntity.ok(salleRepo.findByCapaciteGreaterThanEqual(min).stream().map(this::toDTO).toList());
    }

    // GET /api/v1/salles/batiment/{batiment}
    @GetMapping("/batiment/{batiment}")
    public ResponseEntity<List<SalleDTO>> getByBatiment(@PathVariable String batiment) {
        return ResponseEntity.ok(salleRepo.findByBatiment(batiment).stream().map(this::toDTO).toList());
    }

    // POST /api/v1/salles
    @PostMapping
    public ResponseEntity<SalleDTO> create(@Valid @RequestBody SalleRequest request) {
        Salle salle = Salle.builder()
                .nom(request.getNom())
                .capacite(request.getCapacite())
                .batiment(request.getBatiment())
                .disponible(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(salleRepo.save(salle)));
    }

    // PUT /api/v1/salles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<SalleDTO> update(@PathVariable Long id, @Valid @RequestBody SalleRequest request) {
        Salle salle = salleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle introuvable"));
        salle.setNom(request.getNom());
        salle.setCapacite(request.getCapacite());
        salle.setBatiment(request.getBatiment());
        return ResponseEntity.ok(toDTO(salleRepo.save(salle)));
    }

    // PATCH /api/v1/salles/{id}/disponibilite
    @PatchMapping("/{id}/disponibilite")
    public ResponseEntity<SalleDTO> toggleDisponibilite(@PathVariable Long id, @RequestParam boolean disponible) {
        Salle salle = salleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle introuvable"));
        salle.setDisponible(disponible);
        return ResponseEntity.ok(toDTO(salleRepo.save(salle)));
    }

    // DELETE /api/v1/salles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!salleRepo.existsById(id)) throw new RuntimeException("Salle introuvable");
        salleRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private SalleDTO toDTO(Salle s) {
        return new SalleDTO(s.getId(), s.getNom(), s.getCapacite(), s.isDisponible(), s.getBatiment());
    }
}
