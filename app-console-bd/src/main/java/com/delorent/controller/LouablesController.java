package com.delorent.controller;

import com.delorent.repository.LouableRepository;
import com.delorent.model.FiltrePrixMax;
import com.delorent.model.LouableFiltre;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LouablesController {

    private final LouableRepository louableRepository;

    public LouablesController(LouableRepository louableRepository) {
        this.louableRepository = louableRepository;
    }

    @GetMapping("/louables")
    public String listeLouables(
            @RequestParam(required = false) Double prixMax,
            Model model
    ) {
        List<LouableFiltre> filtres = new ArrayList<>();
        filtres.add(new FiltrePrixMax(prixMax));

        // ✅ appelle TA nouvelle méthode générique
        model.addAttribute("louables", louableRepository.getDisponibles(filtres));

        // ✅ pour garder le champ rempli dans le formulaire
        model.addAttribute("filtrePrixMax", prixMax);

        return "louables";
    }
}
