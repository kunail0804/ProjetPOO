package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.delorent.model.Vehicule;
import com.delorent.repository.VehiculeRepository;

@Controller
public class VehiculeController {

    private final VehiculeRepository vehiculeRepository;

    public VehiculeController(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    // Afficher le formulaire
    @GetMapping("/vehicule/ajouter")
    public String afficherFormulaire(Model model) {
        model.addAttribute("vehicule", new Vehicule());
        return "formulaire_vehicule";
    }

    // Traiter le formulaire
    @PostMapping("/vehicule/enregistrer")
    public String enregistrerVehicule(@ModelAttribute Vehicule vehicule) {
        vehiculeRepository.sauvegarder(vehicule);
        return "redirect:/agent/profil"; // Une fois fini, on retourne au profil pour voir la voiture
    }
}