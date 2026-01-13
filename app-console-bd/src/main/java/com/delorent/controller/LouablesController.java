package com.delorent.controller;

import com.delorent.model.Louable.FiltrePrixMax;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.VehiculeSummary;
import com.delorent.repository.NoteRepository; // Apport de la branche Notation

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LouablesController {

    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository;
    // Ajout du repository de notes (apport branche Notation)
    private final NoteRepository noteRepository;

    public LouablesController(LouableRepository louableRepository,
                              VehiculeRepository vehiculeRepository,
                              NoteRepository noteRepository) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping("/louables")
    public String listeLouables(
            @RequestParam(required = false) Double prixMax,
            @RequestParam(required = false, defaultValue = "false") boolean vehicule,
            @RequestParam(required = false) String date,
            @RequestParam(required = false, defaultValue = "false") boolean uniquementDisponibles,
            Model model
    ) {
        // 1. Gestion de la date (Logique HEAD)
        LocalDate dateCible = LocalDate.now();
        try {
            if (date != null && !date.isBlank()) {
                dateCible = LocalDate.parse(date);
            }
        } catch (Exception ignored) {
            dateCible = LocalDate.now();
        }

        // 2. Préparation des variables pour la Vue
        model.addAttribute("filtrePrixMax", prixMax);
        model.addAttribute("filtreVehicule", vehicule);
        model.addAttribute("filtreDate", dateCible.toString());
        model.addAttribute("filtreUniquementDisponibles", uniquementDisponibles);

        List<LouableFiltre> filtres = new ArrayList<>();
        filtres.add(new FiltrePrixMax(prixMax));

        try {
            if (vehicule) {
                // A. Récupération optimisée (HEAD)
                List<VehiculeSummary> resultats = vehiculeRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);
                
                // B. Calcul des stats et Notes (Intégration branche Notation)
                List<Integer> ids = resultats.stream().map(v -> v.louable().idLouable()).collect(Collectors.toList());
                long availableCount = resultats.stream().filter(v -> v.louable().disponibleAujourdhui()).count();
                
                // Récupération des notes pour ces véhicules
                Map<Integer, Double> moyennesNotes = noteRepository.findMoyennesByLouables(ids);

                // C. Envoi au Model
                model.addAttribute("vehicules", resultats);
                model.addAttribute("louables", null);
                model.addAttribute("moyennesNotes", moyennesNotes); // Les étoiles !
                model.addAttribute("totalCount", resultats.size());
                model.addAttribute("availableCount", availableCount);

            } else {
                // A. Récupération optimisée (HEAD)
                List<LouableSummary> resultats = louableRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);

                // B. Calcul des stats et Notes
                List<Integer> ids = resultats.stream().map(LouableSummary::idLouable).collect(Collectors.toList());
                long availableCount = resultats.stream().filter(LouableSummary::disponibleAujourdhui).count();

                Map<Integer, Double> moyennesNotes = noteRepository.findMoyennesByLouables(ids);

                // C. Envoi au Model
                model.addAttribute("louables", resultats);
                model.addAttribute("vehicules", null);
                model.addAttribute("moyennesNotes", moyennesNotes);
                model.addAttribute("totalCount", resultats.size());
                model.addAttribute("availableCount", availableCount);
            }
            
            return "louables";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("erreur", "Erreur lors du chargement : " + e.getMessage());
            return "louables";
        }
    }
}