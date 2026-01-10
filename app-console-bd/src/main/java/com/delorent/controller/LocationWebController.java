package com.delorent.controller;

import com.delorent.model.Contrat;
import com.delorent.service.ServiceLocation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class LocationWebController {

    private final ServiceLocation serviceLocation;

    public LocationWebController(ServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    @PostMapping("/location/creer")
    public String creerLocation(@RequestParam int idLoueur,
                                @RequestParam int idLouable,
                                @RequestParam int idAssurance,
                                @RequestParam LocalDate dateDebut,
                                @RequestParam LocalDate dateFin,
                                @RequestParam(required = false) String lieuPrise,
                                @RequestParam(required = false) String lieuDepot,
                                RedirectAttributes redirectAttributes) {
        try {
            Contrat contrat = serviceLocation.louer(
                    idLoueur, idLouable, idAssurance,
                    dateDebut, dateFin, lieuDepot
            );

            redirectAttributes.addFlashAttribute("succesLocation",
                    "Location créée (EN_ATTENTE). Lieu prise: " + contrat.getLieuPrise() + " / Lieu dépôt: " + contrat.getLieuDepot());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreurLocation",
                    e.getClass().getSimpleName() + " : " + e.getMessage());
        }

        return "redirect:/";
    }
}