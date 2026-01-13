// FICHIER: src/main/java/com/delorent/controller/NoteController.java
package com.delorent.controller;

import com.delorent.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notation")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/formulaire")
    public String afficherFormulaire(@RequestParam("idContrat") int idContrat, Model model) {

        if (!noteService.peutEtreNote(idContrat)) {
            model.addAttribute("message", "Ce contrat a déjà été noté !");
            return "notation-erreur";
        }

        Map<String, Object> contrat = noteService.getContrat(idContrat);
        if (contrat == null) {
            model.addAttribute("message", "Contrat non trouvé.");
            return "notation-erreur";
        }
        model.addAttribute("contrat", contrat);

        List<Map<String, Object>> criteres = noteService.getCriteres();
        model.addAttribute("criteres", criteres);

        model.addAttribute("idContrat", idContrat);
        return "notation-formulaire";
    }

    @PostMapping("/soumettre")
    public String soumettreNote(@RequestParam("idContrat") int idContrat,
                               @RequestParam(value = "commentaire", required = false, defaultValue = "") String commentaire,
                               @RequestParam Map<String, String> allParams,
                               RedirectAttributes ra) {

        try {
            if (!noteService.peutEtreNote(idContrat)) {
                ra.addFlashAttribute("erreur", "Ce contrat a déjà été noté.");
                return "redirect:/contrats/" + idContrat;
            }

            Map<Integer, Integer> notesCriteres = new HashMap<>();
            for (Map.Entry<String, String> param : allParams.entrySet()) {
                String key = param.getKey();
                if (!key.startsWith("critere_")) continue;

                try {
                    int idCritere = Integer.parseInt(key.replace("critere_", ""));
                    int valeur = Integer.parseInt(param.getValue());
                    notesCriteres.put(idCritere, valeur);
                } catch (NumberFormatException ignore) { }
            }

            if (notesCriteres.isEmpty()) {
                ra.addFlashAttribute("erreur", "Veuillez noter au moins un critère.");
                return "redirect:/notation/formulaire?idContrat=" + idContrat;
            }

            boolean ok = noteService.enregistrerNote(idContrat, notesCriteres, commentaire);

            if (ok) {
                double noteGlobale = noteService.calculerNoteGlobale(notesCriteres);
                ra.addFlashAttribute("succes", "Votre note a été enregistrée en base.");
                ra.addFlashAttribute("noteGlobale", noteGlobale);
            } else {
                ra.addFlashAttribute("erreur", "Ce contrat a déjà été noté.");
            }

            // IMPORTANT : on redirige vers la page contrat EXISTANTE
            return "redirect:/contrats/" + idContrat;

        } catch (Exception e) {
            ra.addFlashAttribute("erreur", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "redirect:/contrats/" + idContrat;
        }
    }
}