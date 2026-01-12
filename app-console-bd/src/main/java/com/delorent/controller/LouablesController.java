package com.delorent.controller;

import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.model.Louable.FiltrePrixMax;
import com.delorent.model.Louable.LouableFiltre;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "false") boolean uniquementDisponibles,
            Model model
    ) {
        LocalDate dateCible = (date == null) ? LocalDate.now() : date;

        List<LouableFiltre> filtres = new ArrayList<>();
        filtres.add(new FiltrePrixMax(prixMax));

        model.addAttribute("filtrePrixMax", prixMax);
        model.addAttribute("filtreVehicule", vehicule);
        model.addAttribute("filtreDate", dateCible);
        model.addAttribute("filtreUniquementDisponibles", uniquementDisponibles);

        if (vehicule) {
            model.addAttribute("vehicules", vehiculeRepository.getCatalogue(dateCible, uniquementDisponibles, filtres));
        } else {
            model.addAttribute("louables", louableRepository.getCatalogue(dateCible, uniquementDisponibles, filtres));
        }

        return "louables";
    }
}