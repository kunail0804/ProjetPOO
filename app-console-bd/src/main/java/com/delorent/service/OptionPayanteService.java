package com.delorent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.delorent.model.OptionPayante;
import com.delorent.repository.OptionPayanteRepository;

@Service
public class OptionPayanteService {

    private final OptionPayanteRepository repo;

    public OptionPayanteService(OptionPayanteRepository repo) {
        this.repo = repo;
    }

    public List<OptionPayante> optionsAvecStatut(int idAgent) {
        return repo.findAllWithAgentStatus(idAgent);
    }

    public void contracter(int idAgent, int idOption) {
        repo.contracter(idAgent, idOption);
    }

    public void annuler(int idAgent, int idOption) {
        repo.annuler(idAgent, idOption);
    }

    public List<OptionPayante> optionsActivesPourAgent(int idAgent) {
        return repo.findActiveOptionsForAgent(idAgent);
    }
}
