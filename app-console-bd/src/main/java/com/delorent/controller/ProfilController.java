package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.delorent.model.Loueur;
import com.delorent.repository.LoueurRepository;

@Controller
public class ProfilController {

    private final LoueurRepository loueurRepository;
    
    // On simule qu'on est connecté en tant que Sophie (ID 2), car l'ID 1 est un Agent
    private final int ID_LOUEUR_CONNECTE = 2; 

    public ProfilController(LoueurRepository loueurRepository) {
        this.loueurRepository = loueurRepository;
    }

    @GetMapping("/profil")
    public String afficherProfil(Model model) {
        // 1. Récupérer les infos complètes du loueur
        Loueur loueur = loueurRepository.trouverParId(ID_LOUEUR_CONNECTE);
        
        // 2. Les envoyer à la vue
        model.addAttribute("loueur", loueur);
        
        return "profil_loueur";
    }
}