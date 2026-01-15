package com.delorent.controller;

import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

        LouableSummary louable = locationService.getLouable(idLouable);
        if (louable == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + idLouable + ").");
            return "louer";
        }

        OffreConvoyage offre = locationService.getOffreActive(idLouable);
        if (offre != null) {
            model.addAttribute("offreSpeciale", offre);
            model.addAttribute("info",
                    "🔥 PROMO : Ce véhicule est en Aller Simple vers " + offre.getVilleParking() + " (Réduction incluse).");
        }

        model.addAttribute("idLouable", idLouable);
        model.addAttribute("louable", louable);
        model.addAttribute("assurances", locationService.getAllAssurances());

        // Pour l'estimation JS (on force un BigDecimal propre)
        model.addAttribute("prixJour", BigDecimal.valueOf(louable.prixJour()).setScale(2, java.math.RoundingMode.HALF_UP));

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
        LouableSummary louable = locationService.getLouable(idLouablePath);
        model.addAttribute("idLouable", idLouablePath);
        model.addAttribute("louable", louable);
        model.addAttribute("assurances", locationService.getAllAssurances());

        if (louable != null) {
            model.addAttribute("prixJour", BigDecimal.valueOf(louable.prixJour()).setScale(2, java.math.RoundingMode.HALF_UP));
        } else {
            model.addAttribute("prixJour", BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP));
        }

        if (louable == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + idLouablePath + ").");
            return "louer";
        }

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
        Integer idLoueurConnecte;
        if (u instanceof Loueur loueurUser) {
            idLoueurConnecte = loueurUser.getIdUtilisateur();
        } else if (u instanceof Agent agentUser) {
            idLoueurConnecte = agentUser.getIdUtilisateur();
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

            if (contrat.getIdParkingRetour() != null) {
                model.addAttribute("succes",
                        "✅ Location validée. ATTENTION : ce véhicule doit être rendu au parking partenaire.");
            } else {
                model.addAttribute("succes", "Location créée (contrat #" + contrat.getId() + ").");
            }

            model.addAttribute("contrat", new ContratView(
                    contrat.getId(),
                    contrat.getDateDebut(),
                    contrat.getDateFin(),
                    contrat.getLieuPrise(),
                    contrat.getLieuDepot(),
                    assuranceNom
            ));

            // Optionnel: exposer le prix final calculé/stocké (si tu veux l’afficher dans le résumé)
            if (contrat.getPrix() != null) {
                model.addAttribute("prixContrat", contrat.getPrix().setScale(2, java.math.RoundingMode.HALF_UP));
            }

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
}