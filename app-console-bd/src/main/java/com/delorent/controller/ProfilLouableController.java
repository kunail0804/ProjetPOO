package com.delorent.controller;

import com.delorent.repository.LouableRepository;
import com.delorent.repository.LouableSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProfilLouableController {

    private final LouableRepository louableRepository;

    public ProfilLouableController(LouableRepository louableRepository) {
        this.louableRepository = louableRepository;
    }

    @GetMapping("/louables/{id}")
    public String profilLouable(@PathVariable("id") int id, Model model) {

        LouableSummary louable = louableRepository.get(id);

        if (louable == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + id + ").");
            return "louable-profil"; // ou "redirect:/louables"
        }

        model.addAttribute("louable", louable);
        return "louable-profil";
    }
}
