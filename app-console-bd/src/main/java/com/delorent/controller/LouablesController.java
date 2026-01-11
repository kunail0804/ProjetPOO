package com.delorent.controller;

import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.model.Louable.FiltrePrixMax;
import com.delorent.model.Louable.LouableFiltre;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            Model model
    ) {
        List<LouableFiltre> filtres = new ArrayList<>();
        filtres.add(new FiltrePrixMax(prixMax));

        model.addAttribute("filtrePrixMax", prixMax);
        model.addAttribute("filtreVehicule", vehicule);

        if (vehicule) {
            model.addAttribute("vehicules", vehiculeRepository.getDisponibles(filtres));
        } else {
            model.addAttribute("louables", louableRepository.getDisponibles(filtres));
        }

        return "louables";
    }
}
