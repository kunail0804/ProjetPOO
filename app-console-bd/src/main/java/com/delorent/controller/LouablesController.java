package com.delorent.controller;

import com.delorent.model.Louable.FiltrePrixMax;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.VehiculeRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LouablesController {

    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository;

    public LouablesController(LouableRepository louableRepository, VehiculeRepository vehiculeRepository) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @GetMapping("/louables")
    public String listeLouables(
            @RequestParam(required = false) Double prixMax,
            @RequestParam(required = false, defaultValue = "false") boolean vehicule,
            @RequestParam(required = false) String date,
            @RequestParam(required = false, defaultValue = "false") boolean uniquementDisponibles,
            Model model
    ) {
        // date par défaut = aujourd’hui
        LocalDate dateCible = LocalDate.now();
        try {
            if (date != null && !date.isBlank()) {
                dateCible = LocalDate.parse(date);
            }
        } catch (Exception ignored) {
            // on garde aujourd’hui
            dateCible = LocalDate.now();
        }

        model.addAttribute("filtrePrixMax", prixMax);
        model.addAttribute("filtreVehicule", vehicule);
        model.addAttribute("filtreDate", dateCible.toString());
        model.addAttribute("filtreUniquementDisponibles", uniquementDisponibles);

        List<LouableFiltre> filtres = new ArrayList<>();
        filtres.add(new FiltrePrixMax(prixMax));

        try {
            if (vehicule) {
                model.addAttribute("vehicules", vehiculeRepository.getCatalogue(dateCible, uniquementDisponibles, filtres));
            } else {
                model.addAttribute("louables", louableRepository.getCatalogue(dateCible, uniquementDisponibles, filtres));
            }
            return "louables";
        } catch (Exception e) {
            // évite le Whitelabel
            model.addAttribute("erreur", "Erreur chargement catalogue: " + e.getMessage());
            model.addAttribute("vehicules", List.of());
            model.addAttribute("louables", List.of());
            return "louables";
        }
    }
}