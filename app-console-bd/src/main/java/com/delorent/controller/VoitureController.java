package com.delorent.controller;

import com.delorent.model.StatutLouable;
import com.delorent.repository.LouableRepository;
import com.delorent.repository.VehiculeRepository;
import com.delorent.repository.VoitureRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voitures")
public class VoitureController {

    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository;
    private final VoitureRepository voitureRepository;

    public VoitureController(LouableRepository louableRepository,
                             VehiculeRepository vehiculeRepository,
                             VoitureRepository voitureRepository) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.voitureRepository = voitureRepository;
    }

    // CREATE
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody VoitureRequest req) {

        // Louable (mère)
        int id = louableRepository.createLouable(
                req.id,
                req.idProprietaire,
                req.marque,
                req.prixJour,
                req.statut == null ? StatutLouable.DISPONIBLE : req.statut,
                req.lieuPrincipal
        );

        // Vehicule (enfant)
        vehiculeRepository.createVehicule(
                id,
                req.modele,
                req.annee,
                req.couleur,
                req.immatriculation,
                req.kilometrage
        );

        // Voiture (spécifique)
        voitureRepository.createVoiture(
                id,
                req.nbPortes,
                req.nbPlaces,
                req.volumeCoffreLitres,
                req.boite,
                req.carburant,
                req.climatisation
        );

        return ResponseEntity.status(201).body(new CreatedResponse(id));
    }

    //  UPDATE 
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable int id,
                                    @RequestBody VoitureRequest req) {

        if (!louableRepository.existsByIdAndProprietaire(id, req.idProprietaire)) {
            return ResponseEntity.status(404).body("Voiture not found or not yours");
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

        voitureRepository.updateVoiture(
                id,
                req.nbPortes,
                req.nbPlaces,
                req.volumeCoffreLitres,
                req.boite,
                req.carburant,
                req.climatisation
        );

        return ResponseEntity.ok("Voiture updated");
    }

    // DELETE 
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable int id,
                                    @RequestParam int idProprietaire) {

        if (!louableRepository.existsByIdAndProprietaire(id, idProprietaire)) {
            return ResponseEntity.status(404).body("Voiture not found or not yours");
        }

        voitureRepository.deleteVoiture(id);
        vehiculeRepository.deleteVehicule(id);
        louableRepository.deleteLouable(id);

        return ResponseEntity.noContent().build();
    }

    //  DTO 
    public static class VoitureRequest {
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

        // Voiture
        public Integer nbPortes;
        public Integer nbPlaces;
        public Integer volumeCoffreLitres;
        public String boite;
        public String carburant;
        public Boolean climatisation;
    }

    public static class CreatedResponse {
        public int id;
        public CreatedResponse(int id) { this.id = id; }
    }
}
