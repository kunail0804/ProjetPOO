package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.delorent.model.Louable.*;
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Utilisateur;

import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.repository.LouableRepository.VoitureRepository;
import com.delorent.repository.LouableRepository.MotoRepository;
import com.delorent.repository.LouableRepository.CamionRepository;

import com.delorent.repository.NoteRepository;
import com.delorent.service.ConnexionService;

@Controller
public class ProfilLouableController {

    private final LouableRepository louableRepository;
    private final VoitureRepository voitureRepository;
    private final MotoRepository motoRepository;
    private final CamionRepository camionRepository;
    private final ConnexionService connexionService;
    private final NoteRepository noteRepository;

    public ProfilLouableController(
            LouableRepository louableRepository,
            VoitureRepository voitureRepository,
            MotoRepository motoRepository,
            CamionRepository camionRepository,
            ConnexionService connexionService,
            NoteRepository noteRepository
    ) {
        this.louableRepository = louableRepository;
        this.voitureRepository = voitureRepository;
        this.motoRepository = motoRepository;
        this.camionRepository = camionRepository;
        this.connexionService = connexionService;
        this.noteRepository = noteRepository;
    }

    @GetMapping("/louables/{id}")
    public String profilLouable(@PathVariable("id") int id, Model model) {

        LouableSummary summary = louableRepository.get(id);
        if (summary == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + id + ").");
            model.addAttribute("canManage", false);
            model.addAttribute("backToProfile", false);

            // Variables attendues par le template (évite les surprises)
            model.addAttribute("noteMoyenne", null);
            model.addAttribute("nbNotes", 0);
            model.addAttribute("noteArrondie", 0);

            return "louable-profil";
        }

        model.addAttribute("louableSummary", summary);
        model.addAttribute("type", summary.type());

        model.addAttribute("isVoiture", "Voiture".equals(summary.type()));
        model.addAttribute("isMoto", "Moto".equals(summary.type()));
        model.addAttribute("isCamion", "Camion".equals(summary.type()));

        // Charger l'objet complet
        Object louable = null;
        switch (summary.type()) {
            case "Voiture" -> louable = voitureRepository.get(id);
            case "Moto" -> louable = motoRepository.get(id);
            case "Camion" -> louable = camionRepository.get(id);
            default -> model.addAttribute("erreur", "Type de louable non supporté : " + summary.type());
        }
        model.addAttribute("louable", louable);

        // ===== NOTATION : moyenne critères par contrat, puis moyenne sur les contrats du louable =====
        Double moy = noteRepository.findMoyenneByLouableFromCriteres(id);
        int nb = noteRepository.countNotesByLouableFromCriteres(id);

        model.addAttribute("noteMoyenne", moy); // Double (null si aucune note)
        model.addAttribute("nbNotes", nb);

        int noteArrondie = (moy == null) ? 0 : (int) Math.round(moy);
        if (noteArrondie < 0) noteArrondie = 0;
        if (noteArrondie > 5) noteArrondie = 5;
        model.addAttribute("noteArrondie", noteArrondie);

        // ----- Gestion rôle + propriétaire -----
        Utilisateur u = connexionService.getConnexion();
        boolean connected = (u != null);
        boolean isAgent = (u instanceof Agent);

        boolean owner = false;
        if (connected && isAgent && louable != null) {
            int idUser = u.getIdUtilisateur();

            // Adapte le getter si besoin:
            if (louable instanceof Vehicule v) {
                owner = (v.getIdAgent() == idUser);
            } else if (louable instanceof Louable l) {
                owner = (l.getIdAgent() == idUser);
            }
        }

        boolean canManage = isAgent && owner;

        model.addAttribute("backToProfile", canManage);
        model.addAttribute("canManage", canManage);

        return "louable-profil";
    }
}