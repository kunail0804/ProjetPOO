package com.delorent.controller;

import com.delorent.model.Louable.*;

import com.delorent.repository.LouableRepository.VoitureRepository;
import com.delorent.repository.LouableRepository.MotoRepository;
import com.delorent.repository.LouableRepository.CamionRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.service.ConnexionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SupprimerLouableController {
    private final VoitureRepository voitureRepository;
    private final MotoRepository motoRepository;
    private final CamionRepository camionRepository;
    private final LouableRepository louableRepository;

    public SupprimerLouableController(
            VoitureRepository voitureRepository,
            MotoRepository motoRepository,
            CamionRepository camionRepository,
            LouableRepository louableRepository
    ) {
        this.voitureRepository = voitureRepository;
        this.motoRepository = motoRepository;
        this.camionRepository = camionRepository;
        this.louableRepository = louableRepository;
    }

    @GetMapping("/louables/{id}/supprimer")
    public String supprimerLouable(@PathVariable("id") int id, Model model) {

        LouableSummary louable = louableRepository.get(id);
        if (louable == null) {
            model.addAttribute("error", "Louable avec l'id " + id + " non trouvé.");
            return "error";
        }

        switch (louable.type()) {
            case "Voiture":
                voitureRepository.delete(id);
                break;
            case "Moto":
                motoRepository.delete(id);
                break;
            case "Camion":
                camionRepository.delete(id);
                break;

            default:
                break;
        }

        return "redirect:/";
    }
}
