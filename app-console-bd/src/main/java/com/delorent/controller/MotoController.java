package com.delorent.controller;

import com.delorent.model.StatutLouable;
import com.delorent.repository.LouableRepository;
import com.delorent.repository.VehiculeRepository;
import com.delorent.repository.MotoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/motos")
public class MotoController {

    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository;
    private final MotoRepository motoRepository;

    public MotoController(LouableRepository louableRepository,
                          VehiculeRepository vehiculeRepository,
                          MotoRepository motoRepository) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.motoRepository = motoRepository;
    }

    // ================= CREATE =================
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody MotoRequest req) {

        // Louable
        int id = louableRepository.createLouable(
                req.id,
                req.idProprietaire,
                req.marque,
                req.prixJour,
                req.statut == null ? StatutLouable.DISPONIBLE : req.statut,
                req.lieuPrincipal
        );

        // Vehicule
        vehiculeRepository.createVehicule(
                id,
                req.modele,
                req.annee,
                req.couleur,
                req.immatriculation,
                req.kilometrage
        );

        // Moto
        motoRepository.createMoto(
                id,
                req.cylindreeCc,
                req.puissanceCh,
                req.typeMoto,
                req.permisRequis
        );

        return ResponseEntity.status(201).body(new CreatedResponse(id));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable int id,
                                    @RequestBody MotoRequest req) {

        if (!louableRepository.existsByIdAndProprietaire(id, req.idProprietaire)) {
            return ResponseEntity.status(404).body("Moto not found or not yours");
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

        motoRepository.updateMoto(
                id,
                req.cylindreeCc,
                req.puissanceCh,
                req.typeMoto,
                req.permisRequis
        );

        return ResponseEntity.ok("Moto updated");
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable int id,
                                    @RequestParam int idProprietaire) {

        if (!louableRepository.existsByIdAndProprietaire(id, idProprietaire)) {
            return ResponseEntity.status(404).body("Moto not found or not yours");
        }

        motoRepository.deleteMoto(id);
        vehiculeRepository.deleteVehicule(id);
        louableRepository.deleteLouable(id);

        return ResponseEntity.noContent().build();
    }

    // ================= DTO =================
    public static class MotoRequest {
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

        // Moto
        public Integer cylindreeCc;
        public Integer puissanceCh;
        public String typeMoto;
        public String permisRequis;
    }

    public static class CreatedResponse {
        public int id;
        public CreatedResponse(int id) { this.id = id; }
    }
}
