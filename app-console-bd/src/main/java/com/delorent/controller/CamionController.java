package com.delorent.controller;

import com.delorent.model.Louable.StatutLouable;
import com.delorent.repository.LouableRepository.CamionRepository;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.VehiculeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/camions")
public class CamionController {

    /**private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository;
    private final CamionRepository camionRepository;

    public CamionController(LouableRepository louableRepository,
                            VehiculeRepository vehiculeRepository,
                            CamionRepository camionRepository) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.camionRepository = camionRepository;
    }

    // ================= CREATE =================
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody CamionRequest req) {

        int id = louableRepository.createLouable(
                req.id,
                req.idProprietaire,
                req.marque,
                req.prixJour,
                req.statut == null ? StatutLouable.DISPONIBLE : req.statut,
                req.lieuPrincipal
        );

        vehiculeRepository.createVehicule(
                id,
                req.modele,
                req.annee,
                req.couleur,
                req.immatriculation,
                req.kilometrage
        );

        camionRepository.createCamion(
                id,
                req.chargeMaxKg,
                req.volumeUtileM3,
                req.hauteurM,
                req.longueurM,
                req.permisRequis
        );

        return ResponseEntity.status(201).body(new CreatedResponse(id));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable int id,
                                    @RequestBody CamionRequest req) {

        if (!louableRepository.existsByIdAndProprietaire(id, req.idProprietaire)) {
            return ResponseEntity.status(404).body("Camion not found or not yours");
        }

        louableRepository.updateLouable(
                id,
                req.idProprietaire,
                req.marque,
                req.prixJour,
                req.statut == null ? StatutLouable.DISPONIBLE : req.statut,
                req.lieuPrincipal
        );

        vehiculeRepository.updateVehicule(
                id,
                req.modele,
                req.annee,
                req.couleur,
                req.immatriculation,
                req.kilometrage
        );

        camionRepository.updateCamion(
                id,
                req.chargeMaxKg,
                req.volumeUtileM3,          
                req.hauteurM,           
                req.longueurM,      
                req.permisRequis    
        );

        return ResponseEntity.ok("Camion updated");
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable int id,
                                    @RequestParam int idProprietaire) {

        if (!louableRepository.existsByIdAndProprietaire(id, idProprietaire)) {
            return ResponseEntity.status(404).body("Camion not found or not yours");
        }

        camionRepository.deleteCamion(id);
        vehiculeRepository.deleteVehicule(id);
        louableRepository.deleteLouable(id);

        return ResponseEntity.noContent().build();
    }

    // ================= DTO =================
    public static class CamionRequest {
        // Louable
        public int id;
        public int idProprietaire;
        public String marque;
        public double prixJour;
        public StatutLouable statut;
        public String lieuPrincipal;

        // Vehicule
        public String modele;
        public int annee;
        public String couleur;
        public String immatriculation;
        public int kilometrage;

        // Camion (spécifique)
        public Integer chargeMaxKg;
        public Double volumeUtileM3;
        public Double hauteurM;
        public Double longueurM;
        public String permisRequis;
    }

    public static class CreatedResponse {
        public int id;
        public CreatedResponse(int id) { this.id = id; }
    }
        **/
}
