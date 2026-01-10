package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vehicule")
public class VehiculeController {
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Plateforme Autopartage");
        model.addAttribute("nombreVehicule", 0);
        return "vehicule-disponible";
    }
    
    @GetMapping("/disponibles")
    public String afficherVehiculesDisponibles(Model model) {
        model.addAttribute("message", "Véhicules disponibles");
        model.addAttribute("nombreVehicule", 0);
        return "vehicule-disponible";
    }
    
    @GetMapping("/tous")
    public String afficherTousLesVehicules(Model model) {
        model.addAttribute("message", "Tous les véhicules");
        model.addAttribute("nombreVehicule", 0);
        return "vehicule-disponible";
    }
}