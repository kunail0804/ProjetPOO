package com.delorent.controller;

import com.delorent.model.Louable.StatutLouable;
import com.delorent.repository.LouableRepository.LouableRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/louables")
public class LouableController {

    /**private final LouableRepository louableRepository;

    public LouableController(LouableRepository louableRepository) {
        this.louableRepository = louableRepository;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody LouableRequest req) {
        int createdId = louableRepository.createLouable(
                req.id,
                req.idProprietaire,
                req.marque,
                req.prixJour,
                req.statut == null ? StatutLouable.DISPONIBLE : req.statut,
                req.lieuPrincipal
        );

        return ResponseEntity.status(201).body(new CreatedResponse(createdId));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody LouableRequest req) {
        // Optionnel : vérifier que c'est bien le bon propriétaire
        if (!louableRepository.existsByIdAndProprietaire(id, req.idProprietaire)) {
            return ResponseEntity.status(404).body("Louable not found or not yours");
        }

        int rows = louableRepository.updateLouable(
                id,
                req.idProprietaire,
                req.marque,
                req.prixJour,
                req.statut == null ? StatutLouable.DISPONIBLE : req.statut,
                req.lieuPrincipal
        );

        if (rows == 0) return ResponseEntity.status(404).body("Louable not found");
        return ResponseEntity.ok("Louable updated");
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id,
                                    @RequestParam(name = "idProprietaire") int idProprietaire) {
        // check ownership
        if (!louableRepository.existsByIdAndProprietaire(id, idProprietaire)) {
            return ResponseEntity.status(404).body("Louable not found or not yours");
        }

        int rows = louableRepository.deleteLouable(id);
        if (rows == 0) return ResponseEntity.status(404).body("Louable not found");
        return ResponseEntity.noContent().build();
    }

    // ===== DTO internes (pour éviter problème d'import) =====
    public static class LouableRequest {
        public int id;
        public int idProprietaire;
        public String marque;
        public double prixJour;
        public StatutLouable statut;
        public String lieuPrincipal;
    }

    public static class CreatedResponse {
        public int id;
        public CreatedResponse(int id) { this.id = id; }
    }
        **/
}

