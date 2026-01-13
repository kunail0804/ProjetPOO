package com.delorent.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping; // Import AJOUTÉ
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.delorent.model.Contrat;
import com.delorent.model.Louable.Disponibilite;
import com.delorent.model.OffreConvoyage;
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Loueur;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.service.ConnexionService;
import com.delorent.service.LocationService;
import com.delorent.vue.ContratView;

@Controller
public class LouerController {

    private final LocationService locationService;
    private final ConnexionService connexionService;

    public LouerController(LocationService locationService, ConnexionService connexionService) {
        this.locationService = locationService;
        this.connexionService = connexionService;
    }

    /* =========================
       GET Page louer
       ========================= */

    @GetMapping("/louables/{id}/louer")
    public String pageLouer(@PathVariable("id") int idLouable, Model model) {

        // Louable (pour résumé + lieuPrincipal)
        LouableSummary louable = locationService.getLouable(idLouable);
        if (louable == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + idLouable + ").");
            return "louer";
        }

        // --- AJOUT : Détection de l'offre Aller Simple ---
        OffreConvoyage offre = locationService.getOffreActive(idLouable);
        if (offre != null) {
            model.addAttribute("offreSpeciale", offre);
            model.addAttribute("info", "🔥 PROMO : Ce véhicule est en Aller Simple vers " + offre.getVilleParking() + " (Réduction incluse).");
        }
        // -------------------------------------------------

        // Listes
        model.addAttribute("idLouable", idLouable);
        model.addAttribute("louable", louable);
        model.addAttribute("assurances", locationService.getAllAssurances());

        return "louer";
    }

    /* =========================
       POST Demande louer
       ========================= */

    @PostMapping("/louables/{id}/louer")
    public String louer(
            @PathVariable("id") int idLouablePath,

            @RequestParam int idLouable,
            @RequestParam int idAssurance,
            @RequestParam LocalDate dateDebut,
            @RequestParam LocalDate dateFin,
            @RequestParam(required = false) String lieuDepotOptionnel,

            Model model
    ) {
        // Toujours recharger ce qui est nécessaire à l'affichage
        LouableSummary louable = locationService.getLouable(idLouablePath);
        model.addAttribute("idLouable", idLouablePath);
        model.addAttribute("louable", louable);
        model.addAttribute("assurances", locationService.getAllAssurances());

        // 1) cohérence URL vs hidden
        if (idLouablePath != idLouable) {
            model.addAttribute("erreur", "Incohérence: idLouable URL != idLouable formulaire.");
            return "louer";
        }

        // 2) utilisateur connecté ?
        if (!connexionService.estConnecte()) {
            model.addAttribute("erreur", "Vous devez être connecté pour louer un véhicule.");
            return "louer";
        }

        Utilisateur u = connexionService.getConnexion();

        // 3) autorisation + récupération idLoueur (celui qui signe le contrat)
        Integer idLoueurConnecte = null;

        if (u instanceof Loueur loueur) {
            idLoueurConnecte = loueur.getIdUtilisateur();
        } else if (u instanceof Agent agent) {
            idLoueurConnecte = agent.getIdUtilisateur();
        } else {
            model.addAttribute("erreur", "Rôle non autorisé pour louer.");
            return "louer";
        }

        // 4) validations dates
        if (dateDebut == null || dateFin == null) {
            model.addAttribute("erreur", "Dates manquantes.");
            return "louer";
        }
        if (!dateDebut.isBefore(dateFin)) {
            model.addAttribute("erreur", "La date de début doit être strictement avant la date de fin.");
            return "louer";
        }

        // 5) appel service + gestion erreurs
        try {
            Contrat contrat = locationService.louer(
                    idLoueurConnecte,
                    idLouable,
                    idAssurance,
                    dateDebut,
                    dateFin,
                    lieuDepotOptionnel
            );

            String assuranceNom = locationService.getAssurance(idAssurance).getNom();

            // --- MODIFICATION : Message personnalisé si Aller Simple ---
            if (contrat.getIdParkingRetour() != null) {
                model.addAttribute("succes", "✅ Location Validée ! ATTENTION : Ce véhicule doit être rendu au parking partenaire.");
            } else {
                model.addAttribute("succes", "Location créée (contrat #" + contrat.getId() + ").");
            }
            // -----------------------------------------------------------

            model.addAttribute("contrat", new ContratView(
                contrat.getId(),
                contrat.getDateDebut(),
                contrat.getDateFin(),
                contrat.getLieuPrise(),
                contrat.getLieuDepot(),
                assuranceNom
            ));

            return "louer";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erreur", e.getMessage());
            return "louer";
        }
    }

    /* =========================
       API JSON dispo
       ========================= */

    @GetMapping("/louer/disponibilites")
    @ResponseBody
    public List<Disponibilite> disponibilites(@RequestParam int idLouable) {
        return locationService.getByLouable(idLouable);
    }
    
    // Je rajoute le Record ici pour être sûr que ça compile (au cas où il manquerait)
    public record ContratView(int id, LocalDate debut, LocalDate fin, String depart, String retour, String assurance) {}
}