package com.delorent.controller;

import com.delorent.model.Utilisateur.Loueur;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.service.ConnexionService;
import com.delorent.service.ContratService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContratController {

    private final ConnexionService connexionService;
    private final ContratService contratService;
    // Plus besoin de UploadProperties ici, c'est le service qui gère !

    public ContratController(ConnexionService connexionService, ContratService contratService) {
        this.connexionService = connexionService;
        this.contratService = contratService;
    }

    @GetMapping("/contrats/{idContrat}/releves")
    public String pageReleves(@PathVariable int idContrat, Model model) {
        Utilisateur u = connexionService.getConnexion();
        if (u == null) return "redirect:/connexion";
        if (!(u instanceof Loueur loueur)) {
            model.addAttribute("erreur", "Accès réservé aux loueurs.");
            return "profil";
        }

        model.addAttribute("releves", contratService.getRelevesContrat(idContrat, loueur.getIdUtilisateur()));
        model.addAttribute("idContrat", idContrat);
        model.addAttribute("peutSaisirPrise", contratService.peutSaisirType(idContrat, loueur.getIdUtilisateur(), "PRISE"));
        model.addAttribute("peutSaisirRetour", contratService.peutSaisirType(idContrat, loueur.getIdUtilisateur(), "RETOUR"));

        return "contrat_releves";
    }

    @PostMapping("/contrats/{idContrat}/releves")
    public String saisirReleve(@PathVariable int idContrat,
                               @RequestParam String typeReleve,
                               @RequestParam int kilometrage,
                               @RequestParam("photo") MultipartFile photo,
                               RedirectAttributes ra) {

        Utilisateur u = connexionService.getConnexion();
        if (u == null) return "redirect:/connexion";
        if (!(u instanceof Loueur loueur)) return "redirect:/profil";

        try {
            // On passe le fichier brut (photo) au service
            contratService.saisirReleve(
                idContrat, 
                loueur.getIdUtilisateur(), 
                typeReleve, 
                kilometrage, 
                photo
            );
            
            ra.addFlashAttribute("succes", "Relevé " + typeReleve + " enregistré avec succès.");

        } catch (IllegalArgumentException e) {
            // Erreurs métier (ex: pas le bon format, km négatif)
            ra.addFlashAttribute("erreur", e.getMessage());
        } catch (Exception e) {
            // Erreurs techniques (ex: disque plein)
            ra.addFlashAttribute("erreur", "Erreur technique : " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/contrats/" + idContrat + "/releves";
    }
}