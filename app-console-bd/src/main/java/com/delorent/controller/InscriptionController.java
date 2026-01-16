package com.delorent.controller;

import com.delorent.service.UtilisateurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class InscriptionController {

    private final UtilisateurService utilisateurService;

    public InscriptionController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }

    private String validerChamps(Map<String, String> champs) {
        Pattern emailP = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Pattern telP = Pattern.compile("^\\+?[0-9 .()-]{6,20}$");
        Pattern codePostalP = Pattern.compile("^[0-9]{4,6}$");
        Pattern siretP = Pattern.compile("^[0-9]{14}$");
        Pattern texteCourtP = Pattern.compile("^[\\p{L}0-9][\\p{L}0-9 '\\-.,]{1,80}$");
        Pattern adresseP = Pattern.compile("^[\\p{L}0-9][\\p{L}0-9 '\\-.,/]{3,120}$");
        Pattern passwordP = Pattern.compile("^.{6,100}$");

        record Rule(String label, Pattern pattern, boolean required, String messageSiInvalide) {}

        Rule[] rules = new Rule[] {
                new Rule("email", emailP, true, "Email invalide."),
                new Rule("password", passwordP, true, "Mot de passe invalide (min 6 caractères)."),

                new Rule("adresse", adresseP, true, "Adresse invalide."),
                new Rule("ville", texteCourtP, true, "Ville invalide."),
                new Rule("codePostal", codePostalP, true, "Code postal invalide."),
                new Rule("region", texteCourtP, true, "Région invalide."),
                new Rule("telephone", telP, true, "Téléphone invalide."),

                new Rule("nom", texteCourtP, false, "Nom invalide."),
                new Rule("prenom", texteCourtP, false, "Prénom invalide."),

                new Rule("nomEntreprise", texteCourtP, false, "Nom d'entreprise invalide."),
                new Rule("raisonSociale", texteCourtP, false, "Raison sociale invalide."),
                new Rule("siret", siretP, false, "SIRET invalide (14 chiffres).")
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

    @GetMapping("/inscription")
    public String inscription(Model model) {
        model.addAttribute("role", "");

        model.addAttribute("email", "");
        model.addAttribute("password", "");
        model.addAttribute("adresse", "");
        model.addAttribute("ville", "");
        model.addAttribute("codePostal", "");
        model.addAttribute("region", "");
        model.addAttribute("telephone", "");

        model.addAttribute("nom", "");
        model.addAttribute("prenom", "");

        model.addAttribute("nomEntreprise", "");
        model.addAttribute("raisonSociale", "");
        model.addAttribute("siret", "");

        return "inscription";
    }

    @PostMapping("/inscription")
    public String inscrire(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String password,

            @RequestParam String adresse,
            @RequestParam String ville,
            @RequestParam String codePostal,
            @RequestParam String region,
            @RequestParam String telephone,

            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,

            @RequestParam(required = false) String nomEntreprise,
            @RequestParam(required = false) String raisonSociale,
            @RequestParam(required = false) String siret,

            Model model
    ) {
        model.addAttribute("role", role);
        model.addAttribute("email", email);

        model.addAttribute("adresse", adresse);
        model.addAttribute("ville", ville);
        model.addAttribute("codePostal", codePostal);
        model.addAttribute("region", region);
        model.addAttribute("telephone", telephone);

        model.addAttribute("nom", nom);
        model.addAttribute("prenom", prenom);

        model.addAttribute("nomEntreprise", nomEntreprise);
        model.addAttribute("raisonSociale", raisonSociale);
        model.addAttribute("siret", siret);

        if (blank(role)) {
            model.addAttribute("error", "Veuillez sélectionner un type de compte.");
            return "inscription";
        }

        if (blank(email) || blank(password) || blank(adresse) || blank(ville) || blank(codePostal) || blank(region) || blank(telephone)) {
            model.addAttribute("error", "Tous les champs communs (email, mot de passe, adresse, ville, code postal, région, téléphone) sont obligatoires.");
            return "inscription";
        }

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

        Map<String, String> champs = new LinkedHashMap<>();
        champs.put("email", email);
        champs.put("password", password);

        champs.put("adresse", adresse);
        champs.put("ville", ville);
        champs.put("codePostal", codePostal);
        champs.put("region", region);
        champs.put("telephone", telephone);

        champs.put("nom", nom);
        champs.put("prenom", prenom);

        champs.put("nomEntreprise", nomEntreprise);
        champs.put("raisonSociale", raisonSociale);
        champs.put("siret", siret);

        String erreurRegex = validerChamps(champs);
        if (erreurRegex != null) {
            model.addAttribute("error", erreurRegex);
            return "inscription";
        }

        try {
            switch (role) {
                case "AGENT" -> utilisateurService.ajouterAgent(
                        email, password,
                        adresse, ville, codePostal, region,
                        telephone,
                        nom, prenom
                );
                case "LOUEUR" -> utilisateurService.ajouterLoueur(
                        email, password,
                        adresse, ville, codePostal, region,
                        telephone,
                        nom, prenom
                );
                case "ENTRETIEN" -> utilisateurService.ajouterEntrepriseEntretien(
                        email, password,
                        adresse, ville, codePostal, region,
                        telephone,
                        nomEntreprise, raisonSociale, siret
                );
                default -> throw new IllegalArgumentException("Rôle inconnu: " + role);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            model.addAttribute("error", "Erreur lors de l'inscription: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return "inscription";
        }

        model.addAttribute("message",
                "Inscription reçue pour " + email +
                        " | rôle=" + role +
                        " | adresse=" + adresse +
                        " | ville=" + ville +
                        " | codePostal=" + codePostal +
                        " | région=" + region +
                        " | téléphone=" + telephone +
                        (("AGENT".equals(role) || "LOUEUR".equals(role))
                                ? (" | nom=" + nom + " | prenom=" + prenom)
                                : "") +
                        ("ENTRETIEN".equals(role)
                                ? (" | entreprise=" + nomEntreprise + " | raisonSociale=" + raisonSociale + " | siret=" + siret)
                                : "")
        );

        return "resultat-inscription";
    }
}
