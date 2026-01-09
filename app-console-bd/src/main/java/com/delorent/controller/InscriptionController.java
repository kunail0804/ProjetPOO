package com.delorent.controller;

import com.delorent.service.UtilisateurService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InscriptionController {

    private final UtilisateurService utilisateurService;

    public InscriptionController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    private boolean validerChampsInscription(String username, String email, String password) {
        return !(username == null || username.isBlank()
              || email == null || email.isBlank()
              || password == null || password.isBlank());
    }

    private boolean estRolePro(String role) {
        if (role == null) return false;
        return role.equals("ENTRETIEN") || role.equals("AGENT_PRO");
    }

    private boolean validerChampsPro(String raisonSociale, String siret) {
        return !(raisonSociale == null || raisonSociale.isBlank()
              || siret == null || siret.isBlank());
    }

    @GetMapping("/inscription")
    public String inscription(Model model) {
        // Valeurs par défaut utiles pour le formulaire
        model.addAttribute("role", "");
        model.addAttribute("username", "");
        model.addAttribute("email", "");
        model.addAttribute("raisonSociale", "");
        model.addAttribute("siret", "");
        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(
            @RequestParam String role,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String raisonSociale,
            @RequestParam(required = false) String siret,
            Model model
    ) {
        // --- Conserver les champs pour réafficher en cas d'erreur ---
        model.addAttribute("role", role);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("raisonSociale", raisonSociale);
        model.addAttribute("siret", siret);

        // --- Validation rôle ---
        if (role == null || role.isBlank()) {
            model.addAttribute("error", "Veuillez sélectionner un type de compte.");
            return "inscription";
        }

        // --- Validation champs communs ---
        if (!validerChampsInscription(username, email, password)) {
            model.addAttribute("error", "Tous les champs (username, email, mot de passe) sont obligatoires.");
            return "inscription";
        }

        // --- Validation champs PRO seulement si rôle pro ---
        if (estRolePro(role)) {
            if (!validerChampsPro(raisonSociale, siret)) {
                model.addAttribute("error", "Pour ce type de compte, la raison sociale et le SIRET sont obligatoires.");
                return "inscription";
            }
        } else {
            // Si pas rôle pro, on ignore complètement ces champs
            raisonSociale = null;
            siret = null;
        }

        switch (role) {
            case "AGENT":
                // TODO: insérer l'agent dans la base de données
                break;

            case "LOUEUR":
                // TODO: insérer le loueur dans la base de données
                break;

            case "ENTRETIEN":
                // TODO: insérer l'entreprise d'entretien dans la base de données
                break;

            case "AGENT_PRO":
                // TODO: insérer l'agent professionnel dans la base de données
                break;

            default:
                break;
        }

        // Pour l’instant on confirme juste la réception
        model.addAttribute("message",
                "Inscription reçue pour " + username +
                " | rôle=" + role +
                (estRolePro(role) ? (" | raisonSociale=" + raisonSociale + " | siret=" + siret) : "")
        );

        return "resultat-inscription";
    }
}