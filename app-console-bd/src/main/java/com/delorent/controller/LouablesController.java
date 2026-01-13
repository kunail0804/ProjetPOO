// FICHIER: src/main/java/com/delorent/controller/LouablesController.java
package com.delorent.controller;

import com.delorent.model.Louable.FiltrePrixMax;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.VehiculeSummary;
import com.delorent.repository.NoteRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

@Controller
public class LouablesController {

    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository;
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
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        LocalDate dateRef = (date != null) ? date : LocalDate.now();

        List<LouableFiltre> filtres = new ArrayList<>();
        filtres.add(new FiltrePrixMax(prixMax));

        Set<Integer> idsDispo = louableRepository.getIdsDisponiblesA(dateRef);

        model.addAttribute("filtrePrixMax", prixMax);
        model.addAttribute("filtreVehicule", vehicule);
        model.addAttribute("dateRef", dateRef);
        model.addAttribute("idsDispo", idsDispo);

        if (vehicule) {
            List<VehiculeSummary> vehicules = new ArrayList<>(vehiculeRepository.getDisponibles(filtres));

            vehicules.sort(
                    Comparator
                            .comparing((VehiculeSummary v) -> !idsDispo.contains(v.louable().idLouable()))
                            .thenComparingInt(v -> v.louable().idLouable())
            );

            int availableCount = 0;
            List<Integer> ids = new ArrayList<>();
            for (VehiculeSummary v : vehicules) {
                int idLouable = v.louable().idLouable();
                ids.add(idLouable);
                if (idsDispo.contains(idLouable)) availableCount++;
            }

            Map<Integer, Double> moyennesNotes = noteRepository.findMoyennesByLouables(ids);

            model.addAttribute("moyennesNotes", moyennesNotes);
            model.addAttribute("vehicules", vehicules);
            model.addAttribute("louables", null);
            model.addAttribute("totalCount", vehicules.size());
            model.addAttribute("availableCount", availableCount);

        } else {
            List<LouableSummary> louables = new ArrayList<>(louableRepository.getDisponibles(filtres));

            louables.sort(
                    Comparator
                            .comparing((LouableSummary l) -> !idsDispo.contains(l.idLouable()))
                            .thenComparingInt(LouableSummary::idLouable)
            );

            int availableCount = 0;
            List<Integer> ids = new ArrayList<>();
            for (LouableSummary l : louables) {
                ids.add(l.idLouable());
                if (idsDispo.contains(l.idLouable())) availableCount++;
            }

            Map<Integer, Double> moyennesNotes = noteRepository.findMoyennesByLouables(ids);

            model.addAttribute("moyennesNotes", moyennesNotes);
            model.addAttribute("louables", louables);
            model.addAttribute("vehicules", null);
            model.addAttribute("totalCount", louables.size());
            model.addAttribute("availableCount", availableCount);
        }

        return "louables";
    }
}