package com.delorent.controller;

import com.delorent.service.ConnexionService;
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Loueur;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.model.Contrat;
import com.delorent.model.Louable.Disponibilite;
import com.delorent.service.LocationService;
import com.delorent.vue.ContratView;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
            // tu peux choisir une page erreur dédiée, ou renvoyer vers catalogue
            return "louer";
            // ou: return "redirect:/louables";
        }

        // Listes
        model.addAttribute("idLouable", idLouable);
        model.addAttribute("louable", louable);
        // TODO faire la gestion complète des assurances
        model.addAttribute("assurances", locationService.getAllAssurances());

        return "louer";
    }

    /* =========================
       POST Demande louer (pas de service pour l'instant)
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

        // 4) validations dates (en plus du service)
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

            model.addAttribute("succes", "Location créée (contrat #" + contrat.getId() + ").");
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
       API JSON dispo (reste pareil mais idéalement on la scoperait sur /louables/{id})
       ========================= */

    @GetMapping("/louer/disponibilites")
    @ResponseBody
    public List<Disponibilite> disponibilites(@RequestParam int idLouable) {
        return locationService.getByLouable(idLouable);
    }
}
