package com.delorent.controller;

import com.delorent.model.ReleveKM.ReleveType;
import com.delorent.model.ReleveKM.ReleveKilometrage;
import com.delorent.model.Utilisateur.Loueur;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.service.ConnexionService;
import com.delorent.service.ContratService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ContratController {

    private final ConnexionService connexionService;
    private final ContratService contratService;

    public ContratController(ConnexionService connexionService, ContratService contratService) {
        this.connexionService = connexionService;
        this.contratService = contratService;
    }

    @GetMapping("/contrats/{idContrat}/releves")
    public String pageReleves(@PathVariable int idContrat, Model model) {

        Utilisateur u = connexionService.getConnexion();
        if (u == null) return "redirect:/connexion";
        if (!(u instanceof Loueur loueur)) {
            model.addAttribute("erreur", "Seul un loueur peut saisir les relevés kilométriques.");
            return "profil"; // ou une page erreur dédiée
        }

        int idLoueur = loueur.getIdUtilisateur();

        List<ReleveKilometrage> releves = contratService.getRelevesContrat(idContrat, idLoueur);

        model.addAttribute("idContrat", idContrat);
        model.addAttribute("releves", releves);

        model.addAttribute("peutSaisirPrise", contratService.peutSaisirType(idContrat, idLoueur, ReleveType.PRISE));
        model.addAttribute("peutSaisirRetour", contratService.peutSaisirType(idContrat, idLoueur, ReleveType.RETOUR));

        return "contrat_releves"; // template à créer
    }

    @PostMapping("/contrats/{idContrat}/releves")
    public String saisirReleve(@PathVariable int idContrat,
                               @RequestParam String typeReleve,
                               @RequestParam int kilometrage,
                               @RequestParam("photo") MultipartFile photo,
                               RedirectAttributes ra) {

        Utilisateur u = connexionService.getConnexion();
        if (u == null) return "redirect:/connexion";
        if (!(u instanceof Loueur loueur)) {
            ra.addFlashAttribute("erreur", "Seul un loueur peut saisir les relevés kilométriques.");
            return "redirect:/profil";
        }

        int idLoueur = loueur.getIdUtilisateur();

        try {
            ReleveType type = ReleveType.valueOf(typeReleve);
            contratService.saisirReleve(idContrat, idLoueur, type, kilometrage, photo);
            ra.addFlashAttribute("succes", "Relevé " + type.name() + " enregistré.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("erreur", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("erreur", "Erreur technique : " + e.getMessage());
        }

        return "redirect:/contrats/" + idContrat + "/releves";
    }
}