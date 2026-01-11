package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.delorent.model.*;
import com.delorent.repository.ContratRepository;
import com.delorent.service.ConnexionService;
import com.delorent.service.UtilisateurService;

@Controller
public class ProfilController {

    private final ConnexionService connexionService;
    private final UtilisateurService utilisateurService;
    private final ContratRepository contratRepository;

    public ProfilController(ConnexionService connexionService, UtilisateurService utilisateurService, ContratRepository contratRepository) {
        this.connexionService = connexionService;
        this.utilisateurService = utilisateurService;
        this.contratRepository = contratRepository;
    }

    @GetMapping("/profil")
    public String afficherProfil(Model model) {

        Utilisateur utilisateur = connexionService.getConnexion();

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        model.addAttribute("utilisateur", utilisateur);

        // Infos génériques (communes à tous)
        model.addAttribute("nomComplet", buildNomComplet(utilisateur));
        model.addAttribute("initiale", buildInitiale(utilisateur));

        // Flags de rôle (simple, lisible côté Thymeleaf)
        model.addAttribute("isLoueur", utilisateur instanceof Loueur);
        model.addAttribute("isAgent", utilisateur instanceof Agent);
        model.addAttribute("isEntretien", utilisateur instanceof EntrepriseEntretien);

        if (utilisateur instanceof Loueur l) {
            model.addAttribute("contrats", contratRepository.getByLoueurId(l.getIdUtilisateur()));
        }

        return "profil";
    }

    @GetMapping("/profil/edition")
    public String editionProfil(Model model) {
        Utilisateur utilisateur = connexionService.getConnexion();
        if (utilisateur == null) return "redirect:/connexion";

        ProfilEditionForm form = new ProfilEditionForm();

        // commun
        form.setMail(utilisateur.getMail());
        form.setAdresse(utilisateur.getAdresse());
        form.setVille(utilisateur.getVille());
        form.setCodePostal(utilisateur.getCodePostal());
        form.setRegion(utilisateur.getRegion());
        form.setTelephone(utilisateur.getTelephone());
        // motDePasse: on ne pré-remplit pas (sécurité). Laisse vide.

        // spécifiques
        if (utilisateur instanceof Loueur l) {
            form.setNom(l.getNom());
            form.setPrenom(l.getPrenom());
        } else if (utilisateur instanceof Agent a) {
            form.setNom(a.getNom());
            form.setPrenom(a.getPrenom());
        } else if (utilisateur instanceof EntrepriseEntretien e) {
            form.setNomEntreprise(e.getNomEntreprise());
            form.setRaisonSoc(e.getRaisonSoc());
            form.setNoSiret(e.getNoSiret());
        }

        model.addAttribute("form", form);

        model.addAttribute("isLoueur", utilisateur instanceof Loueur);
        model.addAttribute("isAgent", utilisateur instanceof Agent);
        model.addAttribute("isEntretien", utilisateur instanceof EntrepriseEntretien);

        model.addAttribute("roleActuel",
                (utilisateur instanceof Agent) ? "AGENT" :
                (utilisateur instanceof Loueur) ? "LOUEUR" : "ENTRETIEN"
        );

        return "profil_edition";
    }

    @PostMapping("/profil/edition")
    public String enregistrerEdition(@ModelAttribute("form") ProfilEditionForm form) {
        Utilisateur utilisateur = connexionService.getConnexion();
        if (utilisateur == null) return "redirect:/connexion";

        // champs communs
        utilisateur.setMail(form.getMail());
        utilisateur.setAdresse(form.getAdresse());
        utilisateur.setVille(form.getVille());
        utilisateur.setCodePostal(form.getCodePostal());
        utilisateur.setRegion(form.getRegion());
        utilisateur.setTelephone(form.getTelephone());

        // mot de passe : seulement si rempli
        if (form.getMotDePasse() != null && !form.getMotDePasse().isBlank()) {
            utilisateur.setMotDePasse(form.getMotDePasse());
        }

        // spécifiques selon rôle
        if (utilisateur instanceof Loueur l) {
            l.setNom(form.getNom());
            l.setPrenom(form.getPrenom());
            utilisateurService.updateLoueur(l);
        } else if (utilisateur instanceof Agent a) {
            a.setNom(form.getNom());
            a.setPrenom(form.getPrenom());
            utilisateurService.updateAgent(a);
        } else if (utilisateur instanceof EntrepriseEntretien e) {
            e.setNomEntreprise(form.getNomEntreprise());
            e.setRaisonSoc(form.getRaisonSoc());
            e.setNoSiret(form.getNoSiret());
            utilisateurService.updateEntrepriseEntretien(e);
        }

        // Optionnel : si update() ne renvoie pas l’objet rechargé
        connexionService.refreshConnexion((long) utilisateur.getIdUtilisateur());

        return "redirect:/profil";
    }

    @PostMapping("/profil/suppression")
    public String supprimerCompte() {
        Utilisateur utilisateur = connexionService.getConnexion();
        if (utilisateur == null) {
            return "redirect:/";
        }

        if(utilisateur instanceof Loueur l) {
            utilisateurService.supprimerLoueur((long) l.getIdUtilisateur());
        } else if (utilisateur instanceof Agent a) {
            utilisateurService.supprimerAgent((long) a.getIdUtilisateur());
        } else if (utilisateur instanceof EntrepriseEntretien e) {
            utilisateurService.supprimerEntrepriseEntretien((long) e.getIdUtilisateur());
        }

        connexionService.deconnecter();
        return "redirect:/";
    }

    // -----------------------
    // Helpers
    // -----------------------

    private String buildNomComplet(Utilisateur u) {
        if (u instanceof Loueur l) {
            return l.getPrenom() + " " + l.getNom();
        }
        if (u instanceof Agent a) {
            return a.getPrenom() + " " + a.getNom();
        }
        if (u instanceof EntrepriseEntretien e) {
            return e.getNomEntreprise();
        }
        return "Utilisateur";
    }

    private String buildInitiale(Utilisateur u) {
        if (u instanceof Loueur l && l.getPrenom() != null) {
            return l.getPrenom().substring(0, 1).toUpperCase();
        }
        if (u instanceof Agent a && a.getPrenom() != null) {
            return a.getPrenom().substring(0, 1).toUpperCase();
        }
        if (u instanceof EntrepriseEntretien e && e.getNomEntreprise() != null) {
            return e.getNomEntreprise().substring(0, 1).toUpperCase();
        }
        return "U";
    }
}
