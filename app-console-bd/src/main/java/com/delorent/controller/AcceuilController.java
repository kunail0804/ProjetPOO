package com.delorent.controller;

import com.delorent.service.ConnexionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcceuilController {
    private final ConnexionService connexionService;

    public AcceuilController(ConnexionService connexionService) {
        this.connexionService = connexionService;
    }

    @GetMapping({"/", "/accueil"})
    public String accueil(Model model) {
        model.addAttribute("estConnecte", connexionService.estConnecte());
        model.addAttribute("utilisateur", connexionService.getConnexion()); // optionnel (afficher nom/role)
        return "accueil";
    }
}
