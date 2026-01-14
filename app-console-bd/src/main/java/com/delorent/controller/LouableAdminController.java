package com.delorent.controller;

import com.delorent.model.Louable.Disponibilite;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.service.DisponibiliteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class LouableAdminController {

    private final LouableRepository louableRepo;
    private final DisponibiliteService dispoService;

    public LouableAdminController(LouableRepository louableRepo, DisponibiliteService dispoService) {
        this.louableRepo = louableRepo;
        this.dispoService = dispoService;
    }

    // Page MODIFIER
    @GetMapping("/louables/{id:\\d+}/modifierDisponibilite")
    public String modifierLouable(@PathVariable("id") int idLouable,
                                  @RequestParam(required = false) String succes,
                                  @RequestParam(required = false) String erreur,
                                  Model model) {

        LouableSummary louable = louableRepo.get(idLouable);
        if (louable == null) {
            return "redirect:/louables?erreur=Louable%20introuvable";
        }

        List<Disponibilite> dispos = dispoService.getByLouable(idLouable);

        model.addAttribute("louable", louable);
        model.addAttribute("idLouable", idLouable);
        model.addAttribute("dispos", dispos);
        model.addAttribute("succes", succes);
        model.addAttribute("erreur", erreur);

        return "modifier_disponibilite";
    }

    // Ajouter une disponibilité (avec MERGE intelligent + contrôles)
    @PostMapping("/louables/{id:\\d+}/disponibilites/add")
    public String addDispo(@PathVariable("id") int idLouable,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        try {
            dispoService.addOrMergeNonReservedRange(idLouable, dateDebut, dateFin);
            return "redirect:/louables/" + idLouable + "/modifier?succes=Disponibilite%20ajoutee";
        } catch (IllegalArgumentException e) {
            return "redirect:/louables/" + idLouable + "/modifier?erreur=" + urlSafe(e.getMessage());
        }
    }

    // Supprimer une disponibilité (si pas de contrat dessus)
    @PostMapping("/louables/{id:\\d+}/disponibilites/delete")
    public String deleteDispo(@PathVariable("id") int idLouable,
                              @RequestParam int idDisponibilite) {

        try {
            dispoService.deleteRangeIfNoContrat(idLouable, idDisponibilite);
            return "redirect:/louables/" + idLouable + "/modifier?succes=Disponibilite%20supprimee";
        } catch (IllegalArgumentException e) {
            return "redirect:/louables/" + idLouable + "/modifier?erreur=" + urlSafe(e.getMessage());
        }
    }

    private static String urlSafe(String s) {
        if (s == null) return "";
        return s.replace(" ", "%20")
                .replace(":", "%3A")
                .replace("'", "%27");
    }
}