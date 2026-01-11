package com.delorent.controller;

import com.delorent.repository.LouableRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LouablesController {

    private final LouableRepository louableRepository;

    public LouablesController(LouableRepository louableRepository) {
        this.louableRepository = louableRepository;
    }

    @GetMapping("/louables")
    public String listeLouables(Model model) {
        model.addAttribute("louables", louableRepository.getAllDisponible());
        return "louables";
    }
}
