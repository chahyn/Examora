package org.examora.examora.examen.controller;

import lombok.RequiredArgsConstructor;
import org.examora.examora.examen.service.ClaudeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class ClaudetestController {
    private final ClaudeService claudeService;
    @GetMapping("/claude")
    public ResponseEntity<String> test(){
        String response = claudeService.ask("Dis bonjour en une phrase.");
        return ResponseEntity.ok(response);
    }
}
