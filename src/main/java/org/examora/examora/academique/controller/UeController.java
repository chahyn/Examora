package org.examora.examora.academique.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.examora.examora.academique.dto.UEDTO;
import org.examora.examora.academique.dto.UERequest;
import org.examora.examora.academique.services.UEService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/ues")
@RequiredArgsConstructor
public class UeController {
    private final UEService ueService;

    @GetMapping
    public ResponseEntity<List<UEDTO>> getAll() {
        return ResponseEntity.ok(ueService.getAll());
    }

    // GET /api/v1/ues/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UEDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ueService.getById(id));
    }

    // GET /api/v1/ues/semestre/{semestreId}
    @GetMapping("/semestre/{semestreId}")
    public ResponseEntity<List<UEDTO>> getBySemestre(@PathVariable Long semestreId) {
        return ResponseEntity.ok(ueService.getBySemestre(semestreId));
    }

    // POST /api/v1/ues
    @PostMapping
    public ResponseEntity<UEDTO> create(@Valid @RequestBody UERequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ueService.creer(request));
    }

    // PUT /api/v1/ues/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UEDTO> update(@PathVariable Long id, @Valid @RequestBody UERequest request) {
        return ResponseEntity.ok(ueService.modifier(id, request));
    }

    // DELETE /api/v1/ues/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ueService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
