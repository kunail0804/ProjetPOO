package com.delorent.controller;

import com.delorent.model.Louable.EntretienTechnique;
import com.delorent.repository.LouableRepository.EntretienTechniqueRepository;
import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.VehiculeSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/vehicles/{vehicleId}/entretiens")
public class EntretienTechniqueController {

    private final EntretienTechniqueRepository entretienRepository;
    private final VehiculeRepository vehiculeRepository;

    public EntretienTechniqueController(
            EntretienTechniqueRepository entretienRepository,
            VehiculeRepository vehiculeRepository) {
        this.entretienRepository = entretienRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    // 1. LISTE des entretiens techniques
    @GetMapping("/")
    public String listeEntretiens(@PathVariable("vehicleId") Integer vehicleId, Model model) {
        VehiculeSummary vehicle = vehiculeRepository.get(vehicleId);
        
        if (vehicle == null) {
            return "redirect:/louables?vehicule=true";
        }
        
        List<EntretienTechnique> entretiens = entretienRepository.findByVehiculeId(vehicleId.longValue());
        
        // Derniers entretiens (5 plus récents)
        List<EntretienTechnique> derniersEntretiens = entretienRepository.findRecentByVehiculeId(vehicleId.longValue(), 5);
        
        // Calcul du coût total des entretiens
        Double coutTotal = entretiens.stream()
            .filter(e -> e.getCout() != null)
            .mapToDouble(EntretienTechnique::getCout)
            .sum();
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("entretiens", entretiens);
        model.addAttribute("derniersEntretiens", derniersEntretiens);
        model.addAttribute("coutTotal", coutTotal);
        model.addAttribute("aujourdhui", LocalDate.now());
        
        return "entretiens-techniques/liste";
    }

    // 2. FORMULAIRE d'ajout
    @GetMapping("/nouveau")
    public String formulaireNouveauEntretien(@PathVariable("vehicleId") Integer vehicleId, Model model) {
        VehiculeSummary vehicle = vehiculeRepository.get(vehicleId);
        
        if (vehicle == null) {
            return "redirect:/louables?vehicule=true";
        }
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("entretienTechnique", new EntretienTechnique());
        
        return "entretiens-techniques/formulaire";
    }

    // 3. ENREGISTRER un nouvel entretien
    @PostMapping("/nouveau")
    public String enregistrerEntretien(@PathVariable("vehicleId") Integer vehicleId,
                                       @ModelAttribute EntretienTechnique entretienTechnique) {
        entretienTechnique.setVehiculeId(vehicleId.longValue());
        entretienRepository.save(entretienTechnique);
        return "redirect:/vehicles/" + vehicleId + "/entretiens/";
    }

    // 4. DÉTAILS d'un entretien
    @GetMapping("/{entretienId}")
    public String detailsEntretien(@PathVariable("vehicleId") Integer vehicleId,
                                   @PathVariable("entretienId") Long entretienId,
                                   Model model) {
        VehiculeSummary vehicle = vehiculeRepository.get(vehicleId);
        var entretienOpt = entretienRepository.findById(entretienId);
        
        if (vehicle == null || entretienOpt.isEmpty()) {
            return "redirect:/vehicles/" + vehicleId + "/entretiens/";
        }
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("entretien", entretienOpt.get());
        
        return "entretiens-techniques/details";
    }

    // 5. SUPPRIMER un entretien
    @PostMapping("/{entretienId}/supprimer")
    public String supprimerEntretien(@PathVariable("vehicleId") Integer vehicleId,
                                     @PathVariable("entretienId") Long entretienId) {
        entretienRepository.deleteById(entretienId);
        return "redirect:/vehicles/" + vehicleId + "/entretiens/";
    }
}