package com.delorent.controller;

import com.delorent.model.Louable.FiltrePrixMax;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary; // Gardé de HEAD
import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.VehiculeSummary; // Gardé de HEAD

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
            @RequestParam(required = false) String date, // On utilise String pour parser manuellement (approche US.L.10)
            @RequestParam(required = false, defaultValue = "false") boolean uniquementDisponibles, // Nouveauté US.L.10
            Model model
    ) {
        // 1. Gestion de la date (Logique US.L.10)
        LocalDate dateCible = LocalDate.now();
        try {
            if (date != null && !date.isBlank()) {
                dateCible = LocalDate.parse(date);
            }
        } catch (Exception ignored) {
            // En cas d'erreur de format, on reste sur aujourd'hui
            dateCible = LocalDate.now();
        }

        // 2. Préparation des filtres
        model.addAttribute("filtrePrixMax", prixMax);
        model.addAttribute("filtreVehicule", vehicule);
        model.addAttribute("filtreDate", dateCible.toString());
        model.addAttribute("filtreUniquementDisponibles", uniquementDisponibles);

        List<LouableFiltre> filtres = new ArrayList<>();
        filtres.add(new FiltrePrixMax(prixMax));

        // 3. Appel aux méthodes optimisées SQL (getCatalogue)
        try {
            if (vehicule) {
                // On appelle la méthode puissante du VehiculeRepository
                List<VehiculeSummary> resultats = vehiculeRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);
                model.addAttribute("vehicules", resultats);
                model.addAttribute("louables", null); // On vide l'autre liste
            } else {
                // On appelle la méthode équivalente du LouableRepository
                List<LouableSummary> resultats = louableRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);
                model.addAttribute("louables", resultats);
                model.addAttribute("vehicules", null);
            }
            return "louables";
            
        } catch (Exception e) {
            // Sécurité : évite d'afficher une page d'erreur blanche à l'utilisateur
            model.addAttribute("erreur", "Erreur lors du chargement du catalogue : " + e.getMessage());
            model.addAttribute("vehicules", List.of());
            model.addAttribute("louables", List.of());
            return "louables";
        }
    }
}