package com.delorent.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.delorent.repository.LouableRepository;

@Controller
public class RechercheLouableController {

    private final LouableRepository louableRepository;

    public RechercheLouableController(LouableRepository louableRepository) {
        this.louableRepository = louableRepository;
    }

    @GetMapping("/recherche")
    public String afficherRecherche(
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) String marque,
            @RequestParam(required = false) Integer annee, // Année Min
            @RequestParam(required = false) Double prix,   // Prix Max
            Model model) {

        // Appel BDD avec les nouveaux critères
        List<Map<String, Object>> resultats = louableRepository.rechercherVehicules(ville, marque, annee, prix);

        // Envoi des données à la vue
        model.addAttribute("vehicules", resultats);
        
        // On renvoie les filtres pour garder le formulaire rempli
        model.addAttribute("filtreVille", ville);
        model.addAttribute("filtreMarque", marque);
        model.addAttribute("filtreAnnee", annee);
        model.addAttribute("filtrePrix", prix);

        return "recherche";
    }
}