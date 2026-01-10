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

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }

    private boolean estRolePro(String role) {
        return "ENTRETIEN".equals(role) || "AGENT_PRO".equals(role);
    }

    @GetMapping("/inscription")
    public String inscription(Model model) {
        model.addAttribute("role", "");
        model.addAttribute("username", "");
        model.addAttribute("email", "");

        // champs personne
        model.addAttribute("nom", "");
        model.addAttribute("prenom", "");

        // champs entreprise
        model.addAttribute("nomEntreprise", "");
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

            // personne
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,

            // entreprise
            @RequestParam(required = false) String nomEntreprise,
            @RequestParam(required = false) String raisonSociale,
            @RequestParam(required = false) String siret,

            Model model
    ) {
        // garder les valeurs pour réaffichage
        model.addAttribute("role", role);
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("nom", nom);
        model.addAttribute("prenom", prenom);
        model.addAttribute("nomEntreprise", nomEntreprise);
        model.addAttribute("raisonSociale", raisonSociale);
        model.addAttribute("siret", siret);

        // validation role
        if (blank(role)) {
            model.addAttribute("error", "Veuillez sélectionner un type de compte.");
            return "inscription";
        }

        // validation commun
        if (blank(username) || blank(email) || blank(password)) {
            model.addAttribute("error", "Tous les champs (username, email, mot de passe) sont obligatoires.");
            return "inscription";
        }

        // validation selon rôle
        if ("AGENT".equals(role) || "LOUEUR".equals(role)) {
            if (blank(nom) || blank(prenom)) {
                model.addAttribute("error", "Pour ce type de compte, le nom et le prénom sont obligatoires.");
                return "inscription";
            }
        }

        if ("ENTRETIEN".equals(role)) {
            if (blank(nomEntreprise) || blank(raisonSociale) || blank(siret)) {
                model.addAttribute("error", "Pour une entreprise d'entretien, nom d'entreprise, raison sociale et SIRET sont obligatoires.");
                return "inscription";
            }
        }

        // appel service (à adapter côté service)
        try {
            switch (role) {
                case "AGENT" -> utilisateurService.ajouterAgent(username, email, password, nom, prenom);
                case "LOUEUR" -> utilisateurService.ajouterLoueur(username, email, password, nom, prenom);
                case "ENTRETIEN" -> utilisateurService.ajouterEntrepriseEntretien(username, email, password, nomEntreprise, raisonSociale, siret);
                // case "AGENT_PRO" -> utilisateurService.ajouterAgentProfessionnel(...);
                default -> throw new IllegalArgumentException("Rôle inconnu: " + role);
            }
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "inscription";
        }

        model.addAttribute("message",
                "Inscription reçue pour " + username +
                " | rôle=" + role +
                (("AGENT".equals(role) || "LOUEUR".equals(role)) ? (" | nom=" + nom + " | prenom=" + prenom) : "") +
                ("ENTRETIEN".equals(role) ? (" | entreprise=" + nomEntreprise + " | raisonSociale=" + raisonSociale + " | siret=" + siret) : "")
        );

        return "resultat-inscription";
    }
}
