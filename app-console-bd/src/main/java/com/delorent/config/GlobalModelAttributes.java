package com.delorent.config;

import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.service.ConnexionService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    private final ConnexionService connexionService;

    public GlobalModelAttributes(ConnexionService connexionService) {
        this.connexionService = connexionService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        boolean estConnecte = connexionService.estConnecte();
        Utilisateur u = connexionService.getConnexion();

        model.addAttribute("estConnecte", estConnecte);
        model.addAttribute("utilisateur", u);

        // pratique pour le menu
        model.addAttribute("isAgent", u instanceof Agent);
    }
}