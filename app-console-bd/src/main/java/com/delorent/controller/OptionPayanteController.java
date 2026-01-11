package com.delorent.controller;

import com.delorent.dao.OptionPayanteDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agent/options")
public class OptionPayanteController {

    private final OptionPayanteDao optionDao;

    public OptionPayanteController(OptionPayanteDao optionDao) {
        this.optionDao = optionDao;
    }

    @GetMapping
    public String afficher(Model model) {
        int idAgent = 1; // simplification assumée
        model.addAttribute("options", optionDao.findByAgent(idAgent));
        return "options-agent";
    }

    @PostMapping("/activer")
    public String activer(@RequestParam int idOption) {
        optionDao.activer(idOption);
        return "redirect:/agent/options";
    }

    @PostMapping("/desactiver")
    public String desactiver(@RequestParam int idOption) {
        optionDao.desactiver(idOption);
        return "redirect:/agent/options";
    }
}

