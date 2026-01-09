package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.delorent.repository.OptionRepository;

@Controller
public class OptionController {

    private final OptionRepository optionRepository;
    private final int ID_AGENT_CONNECTE = 1; // Simulation

    public OptionController(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @GetMapping("/agent/options")
    public String afficherOptionsAgent(Model model) {
        model.addAttribute("options", optionRepository.trouverOptionsPourAgent(ID_AGENT_CONNECTE));
        return "options_agent";
    }

    @PostMapping("/agent/options/changer")
    public String changerEtatOption(@RequestParam(name = "idOption") int idOption, 
                                    @RequestParam(name = "action") String action) {
        if ("activer".equals(action)) {
            optionRepository.souscrire(ID_AGENT_CONNECTE, idOption);
        } else {
            optionRepository.resilier(ID_AGENT_CONNECTE, idOption);
        }
        return "redirect:/agent/options";
    }
}