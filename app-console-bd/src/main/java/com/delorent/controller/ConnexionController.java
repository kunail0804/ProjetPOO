package com.delorent.controller;

import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.service.ConnexionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpSession;


@Controller
public class ConnexionController {

    private final ConnexionService connexionService;

    public ConnexionController(ConnexionService connexionService) {
        this.connexionService = connexionService;
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }

    /**
     * Valide chaque champ via regex (tous les champs passent ici).
     * Retourne null si OK, sinon un message d'erreur.
     */
    private String validerChamps(Map<String, String> champs) {
        Pattern emailP = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        //Pattern passwordP = Pattern.compile("^.{6,100}$"); // min 6//
        Pattern passwordP = Pattern.compile("^.{4,100}$"); // min 4

        record Rule(String label, Pattern pattern, boolean required, String messageSiInvalide) {}

        Rule[] rules = new Rule[] {
                new Rule("role", Pattern.compile("^(AGENT|LOUEUR|ENTRETIEN)$"), true, "Veuillez sélectionner un type de compte valide."),
                new Rule("email", emailP, true, "Email invalide."),
                //new Rule("password", passwordP, true, "Mot de passe invalide (min 6 caractères).")//
                new Rule("password", passwordP, true, "Mot de passe invalide (min 4 caractères).")
        };

        for (Rule r : rules) {
            String v = champs.get(r.label);

            if (r.required && blank(v)) {
                return "Le champ '" + r.label + "' est obligatoire.";
            }
            if (!blank(v) && !r.pattern.matcher(v.trim()).matches()) {
                return r.messageSiInvalide;
            }
        }

        return null;
    }

    @GetMapping("/connexion")
    public String connexion(Model model) {
        model.addAttribute("role", "");
        model.addAttribute("email", "");
        return "connexion";
    }

    @PostMapping("/connexion")
    public String connecter(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            HttpSession session
    ) {
        // garder valeurs pour réaffichage en cas d'erreur
        model.addAttribute("role", role);
        model.addAttribute("email", email);

        // 1) Validation regex (tout passe ici)
        Map<String, String> champs = new LinkedHashMap<>();
        champs.put("role", role);
        champs.put("email", email);
        champs.put("password", password);

        String erreur = validerChamps(champs);
        if (erreur != null) {
            model.addAttribute("error", erreur);
            return "connexion";
        }

        // 2) Connexion via service (stocke l'utilisateur connecté en session)
        try {
            Utilisateur u = connexionService.connecter(role, email, password);

                    // SET SESSION
            session.setAttribute("userId", u.getIdUtilisateur());
            session.setAttribute("role", role); // "LOUEUR"

            // Message de debug (tu pourras remplacer par redirect vers un dashboard)
            model.addAttribute("message",
                    "Connecté: " + u.getMail() + " | rôle=" + role + " | id=" + u.getIdUtilisateur()
            );

            return "resultat-connexion";

        } catch (Exception ex) {
            // Pour analyser côté console/log
            ex.printStackTrace();

            // Pour afficher côté page
            model.addAttribute("error",
                    "Connexion impossible: " + ex.getMessage()
            );
            return "connexion";
        }
    }

    /**
     * Optionnel: endpoint de déconnexion
     */
    @GetMapping("/deconnexion")
    public String deconnexion() {
        connexionService.deconnecter();
        return "redirect:/";
    }
}
