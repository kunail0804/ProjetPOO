package com.delorent.controller;

import com.delorent.dao.NoteDao;
import com.delorent.dao.VehiculeVisiteurDao;
import com.delorent.model.VehiculeVisiteur;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class VehiculeController {

    private final VehiculeVisiteurDao vehiculeDao;
    private final NoteDao noteDao;

    public VehiculeController(VehiculeVisiteurDao vehiculeDao, NoteDao noteDao) {
        this.vehiculeDao = vehiculeDao;
        this.noteDao = noteDao;
    }

    @GetMapping("/vehicule/disponibles")
    public String afficherVehiculesDisponibles(Model model) {

        List<VehiculeVisiteur> vehicules =
                vehiculeDao.findVehiculesDisponibles();

        enrichirAvecNotes(vehicules);

        model.addAttribute("message", "Véhicules disponibles");
        model.addAttribute("nombreVehicules", vehicules.size());
        model.addAttribute("vehicules", vehicules);

        return "vehicules-disponibles";
    }

    @GetMapping("/vehicule/tous")
    public String afficherTousLesVehicules(Model model) {

        List<VehiculeVisiteur> vehicules =
                vehiculeDao.findTousLesVehicules();

        enrichirAvecNotes(vehicules);

        model.addAttribute("message", "Tous les véhicules");
        model.addAttribute("nombreVehicules", vehicules.size());
        model.addAttribute("vehicules", vehicules);

        return "vehicules-disponibles";
    }

    private void enrichirAvecNotes(List<VehiculeVisiteur> vehicules) {
        for (VehiculeVisiteur v : vehicules) {
            try {
                Double note = noteDao.getNoteGlobalePourVehicule(v.getIdLouable());
                v.setNoteGlobale(note != null ? note : 0.0);
            } catch (Exception e) {
                v.setNoteGlobale(0.0);
            }
        }
    }
}
