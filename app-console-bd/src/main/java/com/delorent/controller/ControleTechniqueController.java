package com.delorent.controller;

import com.delorent.model.Louable.ControleTechnique;
import com.delorent.model.Louable.ResultatControle;
import com.delorent.repository.LouableRepository.ControleTechniqueRepository;
import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.VehiculeSummary;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/vehicles/{vehicleId}/controles-techniques")
public class ControleTechniqueController {

    private final ControleTechniqueRepository controleRepository;
    private final VehiculeRepository vehiculeRepository;

    public ControleTechniqueController(
            ControleTechniqueRepository controleRepository,
            VehiculeRepository vehiculeRepository) {
        this.controleRepository = controleRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @GetMapping("/")
    public String listeControles(@PathVariable("vehicleId") Integer vehicleId, Model model) {
        VehiculeSummary vehicle = vehiculeRepository.get(vehicleId);
        
        if (vehicle == null) {
            return "redirect:/louables?vehicule=true";
        }
        
        List<ControleTechnique> controles = controleRepository.findByVehiculeId(vehicleId.longValue());
        ControleTechnique dernierControle = controleRepository.findFirstByVehiculeIdOrderByDateControleDesc(vehicleId.longValue());
        
        boolean controleValide = false;
        if (dernierControle != null && dernierControle.getResultat() == ResultatControle.VALIDE) {
            controleValide = dernierControle.getDateValidite().isAfter(LocalDate.now());
        }
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("controles", controles);
        model.addAttribute("dernierControle", dernierControle);
        model.addAttribute("controleValide", controleValide);
        model.addAttribute("aujourdhui", LocalDate.now());
        
        return "controle-technique/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireNouveauControle(@PathVariable("vehicleId") Integer vehicleId, Model model) {
        VehiculeSummary vehicle = vehiculeRepository.get(vehicleId);
        
        if (vehicle == null) {
            return "redirect:/louables?vehicule=true";
        }
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("controleTechnique", new ControleTechnique());
        model.addAttribute("resultats", ResultatControle.values());
        
        return "controle-technique/nouveau";
    }

    @PostMapping("/nouveau")
    public String enregistrerControle(@PathVariable("vehicleId") Integer vehicleId,
                                      @ModelAttribute ControleTechnique controleTechnique) {
        controleTechnique.setVehiculeId(vehicleId.longValue());
        controleRepository.save(controleTechnique);
        return "redirect:/vehicles/" + vehicleId + "/controles-techniques/";
    }

    @GetMapping("/{controleId}")
    public String detailsControle(@PathVariable("vehicleId") Integer vehicleId,
                                  @PathVariable("controleId") Long controleId,
                                  Model model) {
        VehiculeSummary vehicle = vehiculeRepository.get(vehicleId);
        var controleOpt = controleRepository.findById(controleId);
        
        if (vehicle == null || controleOpt.isEmpty()) {
            return "redirect:/vehicles/" + vehicleId + "/controles-techniques/";
        }
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("controle", controleOpt.get());
        
        return "controle-technique/details";
    }

    @PostMapping("/{controleId}/supprimer")
    public String supprimerControle(@PathVariable("vehicleId") Integer vehicleId,
                                    @PathVariable("controleId") Long controleId) {
        controleRepository.deleteById(controleId);
        return "redirect:/vehicles/" + vehicleId + "/controles-techniques/";
    }
@GetMapping("/simple-test/{id}")
@ResponseBody
public String simpleTest(@PathVariable Integer id) {
    return "Test simple - ID: " + id;
}

}