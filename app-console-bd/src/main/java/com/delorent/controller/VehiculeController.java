package com.delorent.controller;

import com.delorent.model.StatutLouable;
import com.delorent.model.Vehicule;
import com.delorent.repository.VehiculeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller pour gérer l'affichage des véhicules
 * US.V.1 - Consultation des véhicules disponibles
 * 
 * @author Alexia
 */
@Controller
@RequestMapping("/vehicules")
public class VehiculeController {
    
    private final VehiculeRepository vehiculeRepository;
    
    public VehiculeController(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }
    
    /**
     * US.V.1 - Affiche les véhicules disponibles actuellement
     * URL : http://localhost:8080/vehicules/disponibles
     */
    @GetMapping("/disponibles")
    public String afficherVehiculesDisponibles(Model model) {
        
        // 1. Récupère tous les véhicules avec statut DISPONIBLE
        List<Vehicule> tousVehicules = vehiculeRepository.findByStatut(StatutLouable.DISPONIBLE);
        
        // 2. Filtre pour garder seulement ceux disponibles aujourd'hui
        List<Vehicule> vehicules = tousVehicules.stream()
            //.filter(v -> v.estDisponibleMaintenant())
            .collect(Collectors.toList());
        
        model.addAttribute("vehicules", vehicules);
        model.addAttribute("message", "Véhicules disponibles");
        model.addAttribute("nombreVehicules", vehicules.size());
        
        return "vehicules-disponibles";
    }
    
    /**
     * Affiche TOUS les véhicules
     * URL : http://localhost:8080/vehicules/tous
     */
    @GetMapping("/tous")
    public String afficherTousLesVehicules(Model model) {
        
        //List<Vehicule> vehicules = vehiculeRepository.
        //findAll();
        
        //model.addAttribute("vehicules", vehicules);
        //model.addAttribute("message", "Tous les véhicules");
        //model.addAttribute("nombreVehicules", vehicules.size());
        
        return "vehicules-disponibles";
    }
}