package com.delorent.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.delorent.model.OffreConvoyage;
import com.delorent.model.Parking;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.VehiculeRepository; // IMPORTANT : Import ajouté
import com.delorent.repository.LouableRepository.VehiculeSummary;   // IMPORTANT : Import ajouté
import com.delorent.repository.OffreConvoyageRepository;
import com.delorent.repository.ParkingRepository;

@Controller
public class ProfilLouableController {

    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository; // IMPORTANT : Nouveau Repository
    private final ParkingRepository parkingRepository;
    private final OffreConvoyageRepository offreRepository;

    public ProfilLouableController(LouableRepository louableRepository, 
                                   VehiculeRepository vehiculeRepository, // Injecté ici
                                   ParkingRepository parkingRepository,
                                   OffreConvoyageRepository offreRepository) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.parkingRepository = parkingRepository;
        this.offreRepository = offreRepository;
    }

    @GetMapping("/louables/{id}")
    public String profilLouable(@PathVariable("id") int id, Model model) {

        // CORRECTION : On utilise vehiculeRepository pour avoir TOUTES les infos (marque, etc.)
        VehiculeSummary vehicule = vehiculeRepository.get(id);

        if (vehicule == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + id + ").");
            return "louable-profil"; 
        }

        // On envoie 'vehicule' à la vue pour qu'elle puisse lire la marque
        model.addAttribute("vehicule", vehicule);
        
        // On garde 'louable' pour la compatibilité avec le reste de votre HTML
        model.addAttribute("louable", vehicule.louable());

        // --- Gestion Parking / Aller Simple ---
        
        OffreConvoyage offreExistante = offreRepository.getByLouable(id);
        model.addAttribute("offreActuelle", offreExistante);

        if (offreExistante == null) {
            List<Parking> parkings = parkingRepository.getAll();
            model.addAttribute("listeParkings", parkings);
        }

        return "louable-profil";
    }

    @PostMapping("/louables/{id}/offre/ajouter")
    public String ajouterOffre(@PathVariable("id") int idLouable,
                               @RequestParam int idParking,
                               RedirectAttributes redirectAttributes) {
        
        Parking p = parkingRepository.get(idParking);
        
        OffreConvoyage offre = new OffreConvoyage();
        offre.setIdLouable(idLouable);
        offre.setIdParkingArrivee(idParking);
        offre.setReduction(0.10);

        offreRepository.add(offre);

        redirectAttributes.addFlashAttribute("succes", 
            "Offre Aller Simple activée vers " + p.getVille() + ". Coût pour vous : " + p.getPrixAgent() + "€.");
        
        return "redirect:/louables/" + idLouable;
    }

    @PostMapping("/louables/{id}/offre/supprimer")
    public String supprimerOffre(@PathVariable("id") int idLouable,
                                 RedirectAttributes redirectAttributes) {
        
        offreRepository.deleteByLouable(idLouable);
        
        redirectAttributes.addFlashAttribute("info", "L'offre Aller Simple a été retirée.");
        
        return "redirect:/louables/" + idLouable;
    }
}