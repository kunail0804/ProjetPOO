package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.delorent.model.EntrepriseEntretien;
import com.delorent.model.Tarif;
import com.delorent.repository.EntrepriseEntretienRepository;

@Controller
public class EntrepriseController {

    private final EntrepriseEntretienRepository entrepriseRepository;
    
    // Pour le test, on force l'ID de "SpeedyRepair" (ID 4)
    private final int ID_ENTREPRISE_TEST = 4;

    public EntrepriseController(EntrepriseEntretienRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    @GetMapping("/entreprise/tarifs")
    public String gererTarifs(Model model) {
        // 1. Récupérer l'entreprise (utilise l'alias trouverParId(int) qu'on a ajouté)
        EntrepriseEntretien entreprise = entrepriseRepository.trouverParId(ID_ENTREPRISE_TEST);
        model.addAttribute("entreprise", entreprise);

        // 2. Récupérer les tarifs (utilise la nouvelle méthode trouverTarifs)
        model.addAttribute("tarifs", entrepriseRepository.trouverTarifs(ID_ENTREPRISE_TEST));

        // 3. Préparer le formulaire vide
        Tarif nouveauTarif = new Tarif();
        nouveauTarif.setIdEntreprise(ID_ENTREPRISE_TEST);
        model.addAttribute("nouveauTarif", nouveauTarif);

        return "gestion_tarifs";
    }

    @PostMapping("/entreprise/tarifs/ajouter")
    public String ajouterTarif(@ModelAttribute Tarif tarif) {
        // Sécurité pour le test
        tarif.setIdEntreprise(ID_ENTREPRISE_TEST);
        entrepriseRepository.ajouterTarif(tarif);
        return "redirect:/entreprise/tarifs";
    }
}