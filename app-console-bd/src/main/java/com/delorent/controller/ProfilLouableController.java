package com.delorent.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// --- Imports de MAIN (Types spécifiques + Utilisateur) ---
import com.delorent.model.Louable.Louable;
import com.delorent.model.Louable.Vehicule; // Il faudra peut-être l'importer si ce n'est pas dans Louable.*
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Utilisateur;

// --- Imports de PARKING (Offre + Parking) ---
import com.delorent.model.OffreConvoyage;
import com.delorent.model.Parking;

// --- Imports Repositories (Fusion des deux mondes) ---
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.repository.LouableRepository.VehiculeRepository; // De Parking
import com.delorent.repository.LouableRepository.VehiculeSummary;   // De Parking
import com.delorent.repository.LouableRepository.VoitureRepository; // De Main
import com.delorent.repository.LouableRepository.MotoRepository;    // De Main
import com.delorent.repository.LouableRepository.CamionRepository;  // De Main

import com.delorent.repository.OffreConvoyageRepository; // De Parking
import com.delorent.repository.ParkingRepository;        // De Parking

import com.delorent.service.ConnexionService; // De Main

@Controller
public class ProfilLouableController {

    // On déclare TOUT ce dont on a besoin
    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository; // Pour la marque (Fix bug)
    private final VoitureRepository voitureRepository;
    private final MotoRepository motoRepository;
    private final CamionRepository camionRepository;
    
    private final ParkingRepository parkingRepository;       // Pour les offres
    private final OffreConvoyageRepository offreRepository;  // Pour les offres
    
    private final ConnexionService connexionService;         // Pour la sécurité

    // Constructeur Géant (Fusion des deux)
    public ProfilLouableController(
            LouableRepository louableRepository,
            VehiculeRepository vehiculeRepository,
            VoitureRepository voitureRepository,
            MotoRepository motoRepository,
            CamionRepository camionRepository,
            ParkingRepository parkingRepository,
            OffreConvoyageRepository offreRepository,
            ConnexionService connexionService
    ) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.voitureRepository = voitureRepository;
        this.motoRepository = motoRepository;
        this.camionRepository = camionRepository;
        this.parkingRepository = parkingRepository;
        this.offreRepository = offreRepository;
        this.connexionService = connexionService;
    }

    @GetMapping("/louables/{id}")
    public String profilLouable(@PathVariable("id") int id, Model model) {

        // 1. Récupération via VehiculeRepository (Apport de la branche PARKING pour avoir la Marque)
        VehiculeSummary vehiculeSum = vehiculeRepository.get(id);
        
        if (vehiculeSum == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + id + ").");
            model.addAttribute("canManage", false);
            model.addAttribute("backToProfile", false);
            return "louable-profil";
        }

        // On envoie le résumé vehicule (qui contient la marque)
        model.addAttribute("vehicule", vehiculeSum);
        // On garde la logique de summary pour le type
        LouableSummary summary = vehiculeSum.louable();
        model.addAttribute("louableSummary", summary);
        
        // 2. Gestion des types (Apport de la branche MAIN)
        model.addAttribute("type", summary.type());
        model.addAttribute("isVoiture", "Voiture".equals(summary.type()));
        model.addAttribute("isMoto", "Moto".equals(summary.type()));
        model.addAttribute("isCamion", "Camion".equals(summary.type()));

        // Chargement de l'objet complet spécifique (Pour afficher portes, cylindrée, etc.)
        Object specificLouable = null;
        switch (summary.type()) {
            case "Voiture" -> specificLouable = voitureRepository.get(id);
            case "Moto" -> specificLouable = motoRepository.get(id);
            case "Camion" -> specificLouable = camionRepository.get(id);
            default -> model.addAttribute("erreur", "Type non supporté : " + summary.type());
        }
        // "louable" contient l'objet détaillé pour la vue
        model.addAttribute("louable", specificLouable);

        // 3. Gestion des permissions (Apport de la branche MAIN - Indispensable)
        Utilisateur u = connexionService.getConnexion();
        boolean connected = (u != null);
        boolean isAgent = (u instanceof Agent);
        boolean owner = false;

        if (connected && isAgent && specificLouable != null) {
            int idUser = u.getIdUtilisateur();
            // Petite astuce pour vérifier le propriétaire sur l'objet récupéré
            if (specificLouable instanceof com.delorent.model.Louable.Vehicule v) {
                owner = (v.getIdAgent() == idUser); // Vérifiez si c'est getIdProprietaire ou getIdAgent selon votre modèle
            } else if (specificLouable instanceof Louable l) {
                owner = (l.getIdAgent() == idUser);
            }
        }

        boolean canManage = isAgent && owner;
        model.addAttribute("backToProfile", canManage);
        model.addAttribute("canManage", canManage);

        // 4. Gestion Parking / Aller Simple (Apport de la branche PARKING)
        // On charge l'offre quoi qu'il arrive, pour l'affichage
        OffreConvoyage offreExistante = offreRepository.getByLouable(id);
        model.addAttribute("offreActuelle", offreExistante);

        // On ne charge la liste des parkings (pour le formulaire) QUE si on est le propriétaire
        // (Optimisation : inutile de charger la liste pour un client lambda)
        if (canManage && offreExistante == null) {
            List<Parking> parkings = parkingRepository.getAll();
            model.addAttribute("listeParkings", parkings);
        }

        return "louable-profil";
    }

    // --- Actions Offre (Apport de la branche PARKING) ---

    @PostMapping("/louables/{id}/offre/ajouter")
    public String ajouterOffre(@PathVariable("id") int idLouable,
                               @RequestParam int idParking,
                               RedirectAttributes redirectAttributes) {
        
        // Sécurité basique : on pourrait vérifier ici si l'utilisateur est bien le propriétaire
        // mais pour l'instant on fait confiance à l'UI.
        
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