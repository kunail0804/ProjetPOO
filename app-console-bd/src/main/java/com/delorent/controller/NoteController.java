package com.delorent.controller;

import com.delorent.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Controller pour gérer la notation des véhicules.
 * 
 * US.L.3 - Noter un véhicule
 * 
 * MODE TEST : Authentification désactivée temporairement
 */
@Controller
@RequestMapping("/notation")
public class NoteController {
    
    private final NoteService noteService;
    
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    /**
     * Affiche le formulaire de notation pour un contrat donné.
     */
    @GetMapping("/formulaire")
    public String afficherFormulaire(@RequestParam("idContrat") int idContrat, Model model) {
        
        // MODE TEST : Pas de vérification d'authentification
        
        // Vérifier que le contrat n'a pas déjà été noté
        if (!noteService.peutEtreNote(idContrat)) {
            model.addAttribute("message", "Ce contrat a déjà été noté !");
            return "notation-erreur";
        }
        
        // Récupérer les informations du contrat
        Map<String, Object> contrat = noteService.getContrat(idContrat);
        if (contrat == null) {
            model.addAttribute("message", "Contrat non trouvé.");
            return "notation-erreur";
        }
        model.addAttribute("contrat", contrat);
        
        // Récupérer la liste des critères à noter
        List<Map<String, Object>> criteres = noteService.getCriteres();
        model.addAttribute("criteres", criteres);
        
        // Passer l'ID du contrat au formulaire
        model.addAttribute("idContrat", idContrat);
        
        return "notation-formulaire";
    }
    
    /**
     * Traite la soumission du formulaire de notation.
     */
    @PostMapping("/soumettre")
    public String soumettreNote(
            @RequestParam("idContrat") int idContrat,
            @RequestParam(value = "commentaire", required = false, defaultValue = "") String commentaire,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        
        try {
            // MODE TEST : Pas de vérification d'authentification
            
            // Vérifier que le contrat n'est pas déjà noté
            if (!noteService.peutEtreNote(idContrat)) {
                redirectAttributes.addFlashAttribute("erreur", "Ce contrat a déjà été noté.");
                return "redirect:/notation/erreur";
            }
            
            // Extraire les notes des critères
            Map<Integer, Integer> notesCriteres = new HashMap<>();
            
            for (Map.Entry<String, String> param : allParams.entrySet()) {
                String key = param.getKey();
                
                if (key.startsWith("critere_")) {
                    try {
                        int idCritere = Integer.parseInt(key.replace("critere_", ""));
                        int valeurNote = Integer.parseInt(param.getValue());
                        
                        if (valeurNote < 1 || valeurNote > 5) {
                            redirectAttributes.addFlashAttribute("erreur", "Les notes doivent être entre 1 et 5.");
                            return "redirect:/notation/formulaire?idContrat=" + idContrat;
                        }
                        
                        notesCriteres.put(idCritere, valeurNote);
                    } catch (NumberFormatException e) {
                        // Ignorer
                    }
                }
            }
            
            // Vérifier qu'il y a au moins une note
            if (notesCriteres.isEmpty()) {
                redirectAttributes.addFlashAttribute("erreur", "Veuillez noter au moins un critère.");
                return "redirect:/notation/formulaire?idContrat=" + idContrat;
            }
            
            // Enregistrer la note
            boolean succes = noteService.enregistrerNote(idContrat, notesCriteres, commentaire);
            
            if (succes) {
                double noteGlobale = noteService.calculerNoteGlobale(notesCriteres);
                redirectAttributes.addFlashAttribute("noteGlobale", noteGlobale);
                redirectAttributes.addFlashAttribute("message", "Votre note a été enregistrée avec succès !");
                return "redirect:/notation/confirmation";
            } else {
                redirectAttributes.addFlashAttribute("erreur", "Ce contrat a déjà été noté.");
                return "redirect:/notation/erreur";
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erreur", "Une erreur est survenue : " + e.getMessage());
            return "redirect:/notation/erreur";
        }
    }
    
    /**
     * Page de confirmation.
     */
    @GetMapping("/confirmation")
    public String afficherConfirmation() {
        return "notation-confirmation";
    }
    
    /**
     * Page d'erreur.
     */
    @GetMapping("/erreur")
    public String afficherErreur() {
        return "notation-erreur";
    }
}