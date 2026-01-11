package com.delorent.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.delorent.model.Agent;
import com.delorent.model.Vehicule;
import com.delorent.repository.AgentRepository;

@Controller
public class AgentController {

    private final AgentRepository agentRepository;

    public AgentController(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @GetMapping("/agent/profil")
    public String voirProfilAgent(@RequestParam(defaultValue = "1") int id, Model model) {
        // 1. On charge l'agent
        Agent agent = agentRepository.trouverAgentParId(id);
        model.addAttribute("agent", agent);

        // 2. On charge ses véhicules
        List<Vehicule> vehicules = agentRepository.trouverVehiculesParAgent(id);
        model.addAttribute("vehicules", vehicules);

        return "profil_agent_public";
    }
}