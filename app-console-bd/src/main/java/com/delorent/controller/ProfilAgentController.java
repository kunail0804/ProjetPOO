package com.delorent.controller;

import com.delorent.model.Utilisateur.Agent;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.AgentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProfilAgentController {

    private final LouableRepository louableRepository;
    private final AgentRepository agentRepository;

    public ProfilAgentController(LouableRepository louableRepository,AgentRepository agentRepository) {
        this.louableRepository = louableRepository;
        this.agentRepository = agentRepository;
    }

    @GetMapping("/profilAgent/{id}")
    public String profilAgentPublic(@PathVariable("id") long id, Model model) {

        // 1) Charger l'agent
        Agent agent = agentRepository.get(id);
        if (agent == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agent introuvable");
        }

        // 2) Charger ses louables (ce que tu veux rendre public)
        model.addAttribute("agent", agent);
        model.addAttribute("nomComplet", agent.getPrenom() + " " + agent.getNom());
        model.addAttribute("initiale", safeInitiale(agent.getPrenom()));

        model.addAttribute("louables", louableRepository.getByProprietaire(agent.getIdUtilisateur()));

        return "profil_agent";
    }

    private String safeInitiale(String prenom) {
        if (prenom == null || prenom.isBlank()) return "A";
        return prenom.substring(0, 1).toUpperCase();
    }
}
