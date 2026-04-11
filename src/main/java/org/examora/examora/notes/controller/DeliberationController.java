package org.examora.examora.notes.controller;

import lombok.RequiredArgsConstructor;
import org.examora.examora.notes.dto.DeliberationResultDTO;
import org.examora.examora.notes.service.DeliberationService;
import org.examora.examora.utilisateur.entities.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/admin/deliberation")
@RequiredArgsConstructor
public class DeliberationController {
    private final DeliberationService deliberationService;
    @PostMapping("/lancer")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<DeliberationResultDTO> lancerDeliberation(
            @RequestParam Long semestreId,
            @RequestParam String anneeUniversitaire,
            @AuthenticationPrincipal Utilisateur admin) {
        DeliberationResultDTO result = deliberationService
                .lancerDeliberation(semestreId, anneeUniversitaire, admin);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{semestreId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'PROFESSEUR')")
    public ResponseEntity<DeliberationResultDTO> getDeliberation(
            @PathVariable Long semestreId,
            @RequestParam String anneeUniversitaire) {
        return ResponseEntity.ok(
                deliberationService.getDeliberation(semestreId, anneeUniversitaire));
    }
    @GetMapping("/{semestreId}/resume")
    @PreAuthorize("hasAnyRole('ADMINISTRATEUR', 'PROFESSEUR')")
    public ResponseEntity<Map<String, Object>> getResume(
            @PathVariable Long semestreId,
            @RequestParam String anneeUniversitaire) {
        DeliberationResultDTO result = deliberationService
                .getDeliberation(semestreId, anneeUniversitaire);
        return ResponseEntity.ok(Map.of(
                "semestreId",         result.getSemestreId(),
                "anneeUniversitaire", result.getAnneeUniversitaire(),
                "nbEtudiants",        result.getNbEtudiants(),
                "nbAdmis",            result.getNbAdmis(),
                "nbAjournes",         result.getNbAjournes(),
                "nbRedoubles",        result.getNbRedoubles(),
                "tauxReussite",       result.getTauxReussite(),
                "moyennePromo",       result.getMoyennePromo()
        ));
    }
}
