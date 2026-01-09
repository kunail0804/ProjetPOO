package com.delorent.controller;

import com.delorent.repository.DisponibiliteRepository;
import com.delorent.repository.LocationRepository;
import com.delorent.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class LouerWebController {

    private final LocationRepository locationRepo;
    private final DisponibiliteRepository dispoRepo;
    private final LocationService locationService;

    public LouerWebController(LocationRepository locationRepo,
                              DisponibiliteRepository dispoRepo,
                              LocationService locationService) {
        this.locationRepo = locationRepo;
        this.dispoRepo = dispoRepo;
        this.locationService = locationService;
    }

    @GetMapping("/louer")
    public String pageLouer(Model model) {
        chargerListes(model);
        return "louer";
    }

    @PostMapping("/louer")
    public String louer(Model model,
                        @RequestParam int idLoueur,
                        @RequestParam int idLouable,
                        @RequestParam(required = false) Integer idAssurance,
                        @RequestParam LocalDate dateDebut,
                        @RequestParam LocalDate dateFin,
                        @RequestParam(required = false) String lieuDepotOptionnel) {

        chargerListes(model);

        try {
            int idContrat = locationService.creerContrat(
                    idLoueur, idLouable, idAssurance, dateDebut, dateFin, lieuDepotOptionnel
            );

            Map<String, Object> contrat = locationRepo.getContrat(idContrat);
            model.addAttribute("contrat", contrat);
            model.addAttribute("succes", "Contrat créé (id=" + idContrat + ").");

        } catch (Exception e) {
            model.addAttribute("erreur", e.getMessage());
        }

        return "louer";
    }

    @GetMapping("/louer/disponibilites")
    @ResponseBody
    public List<Map<String, Object>> disponibilites(@RequestParam int idLouable) {
        return dispoRepo.findByLouable(idLouable);
    }

    private void chargerListes(Model model) {
        model.addAttribute("loueurs", locationRepo.findLoueurs());
        model.addAttribute("louables", locationRepo.findLouablesAvecVehicule());
        model.addAttribute("assurances", locationRepo.findAssurances());
    }
}