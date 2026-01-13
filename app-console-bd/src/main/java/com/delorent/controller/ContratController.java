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
@RequestMapping("/contrats")
public class ContratController {

    private final ConnexionService connexionService;
    private final ContratService contratService;

    public ContratController(ConnexionService connexionService, ContratService contratService) {
        this.connexionService = connexionService;
        this.contratService = contratService;
    }

    // --- AFFICHER LA PAGE DES RELEVÉS (GET) ---
    @GetMapping("/{idContrat}/releves")
    public String pageReleves(@PathVariable int idContrat, Model model) {
        Utilisateur u = connexionService.getConnexion();
        if (u == null) return "redirect:/connexion";
        
        // Vérification : Seul un Loueur peut voir ses relevés
        if (!(u instanceof Loueur loueur)) {
            return "redirect:/profil";
        }

        try {
            // Le service s'occupe de tout récupérer (et vérifie si le contrat appartient bien au loueur)
            // Note: On suppose que tu as une méthode getRelevesContrat ou similaire dans le service.
            // Si elle n'existe pas, le service doit renvoyer les infos nécessaires.
            
            // Pour l'instant, on utilise les méthodes simples du service :
            boolean peutPrise = contratService.peutSaisirType(idContrat, loueur.getIdUtilisateur(), "PRISE");
            boolean peutRetour = contratService.peutSaisirType(idContrat, loueur.getIdUtilisateur(), "RETOUR");
            
            model.addAttribute("idContrat", idContrat);
            model.addAttribute("peutSaisirPrise", peutPrise);
            model.addAttribute("peutSaisirRetour", peutRetour);
            
            // On récupère aussi la liste des relevés existants pour l'affichage
            model.addAttribute("releves", contratService.getRelevesContrat(idContrat, loueur.getIdUtilisateur()));

            return "contrat_releves"; // Assure-toi que ce fichier HTML existe

        } catch (IllegalArgumentException e) {
            // Si le contrat n'est pas trouvé ou pas au loueur
            return "redirect:/profil";
        }
    }

    // --- ENREGISTRER UN RELEVÉ (POST) ---
    @PostMapping("/{idContrat}/releves")
    public String saisirReleve(@PathVariable int idContrat,
                               @RequestParam String typeReleve,
                               @RequestParam int kilometrage,
                               @RequestParam("photo") MultipartFile photo,
                               RedirectAttributes ra) {

        Utilisateur u = connexionService.getConnexion();
        if (u == null) return "redirect:/connexion";
        if (!(u instanceof Loueur loueur)) return "redirect:/profil";

        try {
            // APPEL AU SERVICE : C'est lui qui gère la logique métier (validation km, upload fichier, insert bdd)
            contratService.saisirReleve(
                idContrat, 
                loueur.getIdUtilisateur(), 
                typeReleve, 
                kilometrage, 
                photo
            );
            
            ra.addFlashAttribute("succes", "Relevé " + typeReleve + " enregistré avec succès !");

        } catch (IllegalArgumentException e) {
            // Erreurs métier (ex: "Kilométrage incohérent", "Photo manquante")
            ra.addFlashAttribute("erreur", e.getMessage());
        } catch (Exception e) {
            // Erreurs techniques imprévues
            e.printStackTrace();
            ra.addFlashAttribute("erreur", "Erreur technique lors de l'enregistrement.");
        }

        // On redirige vers la page GET pour afficher le résultat
        return "redirect:/contrats/" + idContrat + "/releves";
    }
}