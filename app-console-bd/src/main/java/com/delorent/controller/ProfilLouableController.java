package com.delorent.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.delorent.model.Louable.Louable;
import com.delorent.model.Louable.Vehicule;
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.model.OffreConvoyage;
import com.delorent.model.Parking;

import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.repository.LouableRepository.VehiculeRepository;
import com.delorent.repository.LouableRepository.VehiculeSummary;
import com.delorent.repository.LouableRepository.VoitureRepository;
import com.delorent.repository.LouableRepository.MotoRepository;
import com.delorent.repository.LouableRepository.CamionRepository;
import com.delorent.repository.OffreConvoyageRepository;
import com.delorent.repository.ParkingRepository;
import com.delorent.repository.NoteRepository;

import com.delorent.service.ConnexionService;

@Controller
public class ProfilLouableController {

    private final LouableRepository louableRepository;
    private final VehiculeRepository vehiculeRepository;
    private final VoitureRepository voitureRepository;
    private final MotoRepository motoRepository;
    private final CamionRepository camionRepository;
    
    private final ParkingRepository parkingRepository;
    private final OffreConvoyageRepository offreRepository;
    private final NoteRepository noteRepository;
    
    private final ConnexionService connexionService;

    public ProfilLouableController(
            LouableRepository louableRepository,
            VehiculeRepository vehiculeRepository,
            VoitureRepository voitureRepository,
            MotoRepository motoRepository,
            CamionRepository camionRepository,
            ParkingRepository parkingRepository,
            OffreConvoyageRepository offreRepository,
            NoteRepository noteRepository,
            ConnexionService connexionService
    ) {
        this.louableRepository = louableRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.voitureRepository = voitureRepository;
        this.motoRepository = motoRepository;
        this.camionRepository = camionRepository;
        this.parkingRepository = parkingRepository;
        this.offreRepository = offreRepository;
        this.noteRepository = noteRepository;
        this.connexionService = connexionService;
    }

    @GetMapping("/louables/{id}")
    public String profilLouable(@PathVariable("id") int id, Model model) {

        VehiculeSummary vehiculeSum = vehiculeRepository.get(id);
        
        if (vehiculeSum == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + id + ").");
            model.addAttribute("canManage", false);
            model.addAttribute("backToProfile", false);
            model.addAttribute("noteMoyenne", null);
            model.addAttribute("nbNotes", 0);
            model.addAttribute("noteArrondie", 0);
            return "louable-profil";
        }

        model.addAttribute("vehicule", vehiculeSum);
        LouableSummary summary = vehiculeSum.louable();
        model.addAttribute("louableSummary", summary);
        
        model.addAttribute("type", summary.type());
        model.addAttribute("isVoiture", "Voiture".equals(summary.type()));
        model.addAttribute("isMoto", "Moto".equals(summary.type()));
        model.addAttribute("isCamion", "Camion".equals(summary.type()));

        Object specificLouable = null;
        switch (summary.type()) {
            case "Voiture" -> specificLouable = voitureRepository.get(id);
            case "Moto" -> specificLouable = motoRepository.get(id);
            case "Camion" -> specificLouable = camionRepository.get(id);
            default -> model.addAttribute("erreur", "Type non supporté : " + summary.type());
        }
        model.addAttribute("louable", specificLouable);

        Double moy = noteRepository.findMoyenneByLouableFromCriteres(id);
        int nb = noteRepository.countNotesByLouableFromCriteres(id);

        model.addAttribute("noteMoyenne", moy);
        model.addAttribute("nbNotes", nb);

        int noteArrondie = (moy == null) ? 0 : (int) Math.round(moy);
        if (noteArrondie < 0) noteArrondie = 0;
        if (noteArrondie > 5) noteArrondie = 5;
        model.addAttribute("noteArrondie", noteArrondie);

        Utilisateur u = connexionService.getConnexion();
        boolean connected = (u != null);
        boolean isAgent = (u instanceof Agent);
        boolean owner = false;

        if (connected && isAgent && specificLouable != null) {
            int idUser = u.getIdUtilisateur();
            if (specificLouable instanceof Vehicule v) {
                owner = (v.getIdAgent() == idUser);
            } else if (specificLouable instanceof Louable l) {
                owner = (l.getIdAgent() == idUser);
            }
        }

        boolean canManage = isAgent && owner;
        model.addAttribute("backToProfile", canManage);
        model.addAttribute("canManage", canManage);

        OffreConvoyage offreExistante = offreRepository.getByLouable(id);
        model.addAttribute("offreActuelle", offreExistante);

        if (canManage && offreExistante == null) {
            List<Parking> parkings = parkingRepository.getAll();
            model.addAttribute("listeParkings", parkings);
        }

        return "louable-profil";
    }

    @PostMapping("/louables/{id}/offre/ajouter")
    public String ajouterOffre(@PathVariable("id") int idLouable,
                               @RequestParam int idParking,
                               RedirectAttributes redirectAttributes) {
        
        Parking p = parkingRepository.get(idParking);
        
        OffreConvoyage offre = new OffreConvoyage();
        offre.setIdLouable(idLouable);
        offre.setIdParkingArrivee(idParking);
        offre.setReduction(0.10);

        offreRepository.add(offre);

        redirectAttributes.addFlashAttribute("succes", 
            "Offre Aller Simple activée vers " + p.getVille() + ". Coût pour vous : " + p.getPrixAgent() + "€.");
        
        return "redirect:/louables/" + idLouable;
    }

    @PostMapping("/louables/{id}/offre/supprimer")
    public String supprimerOffre(@PathVariable("id") int idLouable,
                                 RedirectAttributes redirectAttributes) {
        
        offreRepository.deleteByLouable(idLouable);
        redirectAttributes.addFlashAttribute("info", "L'offre Aller Simple a été retirée.");
        return "redirect:/louables/" + idLouable;
    }
}