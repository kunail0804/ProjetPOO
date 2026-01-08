package com.delorent.controller;

import com.delorent.repository.VehiculeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent/vehicules")
public class AgentVehiculeController {

    private final VehiculeRepository vehiculeRepository;

    public AgentVehiculeController(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    /**
     * AJOUTER un véhicule
     */
    @PostMapping
    public ResponseEntity<?> addVehicule(@RequestBody VehiculeRequest request) {

        int rows = vehiculeRepository.addVehicule(
                request.id,
                request.marque,
                request.modele,
                request.immatriculation
        );

        if (rows == 1) {
            return ResponseEntity.status(201).body("Vehicule ajouté");
        }
        return ResponseEntity.badRequest().body("Erreur lors de l'ajout");
    }

    /**
     * MODIFIER un véhicule
     */
    @PutMapping("/{idLouable}")
    public ResponseEntity<?> updateVehicule(@PathVariable int idLouable,
                                            @RequestBody VehiculeRequest request) {

        int rows = vehiculeRepository.updateVehicule(
                request.id,
                request.marque,
                request.modele,
                request.immatriculation
        );

        if (rows == 1) {
            return ResponseEntity.ok("Vehicule modifié");
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * SUPPRIMER un véhicule
     */
    @DeleteMapping("/{idLouable}")
    public ResponseEntity<?> deleteVehicule(@PathVariable int idLouable) {

        int rows = vehiculeRepository.deleteVehicule(idLouable);

        if (rows == 1) {
            return ResponseEntity.ok("Vehicule supprimé");
        }
        return ResponseEntity.notFound().build();
    }
}

class VehiculeRequest {
    public int id;
    public String marque;
    public String modele;
    public String immatriculation;
}