
package com.delorent.controller;

import com.delorent.service.OptionPayanteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OptionAgentController {

    private final OptionPayanteService service;

    public OptionAgentController(OptionPayanteService service) {
        this.service = service;
    }

    @GetMapping("/agent/options")
    public String page(@RequestParam int idAgent, Model model) {
        model.addAttribute("idAgent", idAgent);
        model.addAttribute("options", service.optionsAvecStatut(idAgent));
        return "option-agent";
    }

    @PostMapping("/agent/options/{idOption}/contracter")
    public String contracter(@RequestParam int idAgent, @PathVariable int idOption) {
        service.contracter(idAgent, idOption);
        return "redirect:/agent/options?idAgent=" + idAgent;
    }

    @PostMapping("/agent/options/{idOption}/annuler")
    public String annuler(@RequestParam int idAgent, @PathVariable int idOption) {
        service.annuler(idAgent, idOption);
        return "redirect:/agent/options?idAgent=" + idAgent;
    }
}






