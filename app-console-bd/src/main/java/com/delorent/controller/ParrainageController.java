package com.delorent.controller;

import com.delorent.service.ParrainageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/parrainage")
public class ParrainageController {

    private final ParrainageService parrainageService;

    @Autowired
    public ParrainageController(ParrainageService parrainageService) {
        this.parrainageService = parrainageService;
    }

    // PAGE parrainage
    @GetMapping
    public String pageParrainage(HttpSession session, Model model) {
        Integer idFilleul = (Integer) session.getAttribute("loueurId");
        if (idFilleul == null) {
            idFilleul = (Integer) session.getAttribute("userId");
        }

        if (idFilleul == null) {
            model.addAttribute("message", "Veuillez vous connecter.");
            return "parrainage";
        }

        model.addAttribute("idFilleul", idFilleul);
        // double credit = parrainageService.getCreditLoueur(idFilleul);
        // model.addAttribute("credit", credit);

        return "parrainage";
    }

    // submit code parrainage
    @PostMapping("/ajouter")
    public String ajouterParrainage(@RequestParam("codeParrain") int idParrain,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        try {
            Integer idFilleul = (Integer) session.getAttribute("loueurId");
            if (idFilleul == null) {
                idFilleul = (Integer) session.getAttribute("userId");
            }
            if (idFilleul == null) {
                throw new IllegalStateException("Vous devez être connecté pour parrainer.");
            }

            parrainageService.parrainer(idParrain, idFilleul);
            redirectAttributes.addFlashAttribute("message", "Code parrainage ajouté avec succès !");
            return "redirect:/parrainage";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/parrainage";
        }
    }
}
