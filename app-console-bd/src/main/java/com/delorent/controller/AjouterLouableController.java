// FICHIER: src/main/java/com/delorent/controller/AjouterLouableController.java
package com.delorent.controller;

import com.delorent.model.Louable.*;
import com.delorent.repository.LouableRepository.CamionRepository;
import com.delorent.repository.LouableRepository.MotoRepository;
import com.delorent.repository.LouableRepository.VoitureRepository;
import com.delorent.service.ConnexionService;
import com.delorent.service.DisponibiliteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class AjouterLouableController {

    private final VoitureRepository voitureRepository;
    private final MotoRepository motoRepository;
    private final CamionRepository camionRepository;
    private final ConnexionService connexionService;
    private final DisponibiliteService disponibiliteService;

    public AjouterLouableController(
            VoitureRepository voitureRepository,
            MotoRepository motoRepository,
            CamionRepository camionRepository,
            ConnexionService connexionService,
            DisponibiliteService disponibiliteService
    ) {
        this.voitureRepository = voitureRepository;
        this.motoRepository = motoRepository;
        this.camionRepository = camionRepository;
        this.connexionService = connexionService;
        this.disponibiliteService = disponibiliteService;
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }

    private Integer toInt(String s, String field) {
        if (blank(s)) return null;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Champ '" + field + "' invalide (entier attendu)."); }
    }

    private Double toDouble(String s, String field) {
        if (blank(s)) return null;
        try { return Double.parseDouble(s.trim().replace(',', '.')); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Champ '" + field + "' invalide (nombre attendu)."); }
    }

    private Boolean toBooleanNullable(String s, String field) {
        if (blank(s)) return null;
        if ("true".equalsIgnoreCase(s.trim())) return true;
        if ("false".equalsIgnoreCase(s.trim())) return false;
        throw new IllegalArgumentException("Champ '" + field + "' invalide (true/false attendu).");
    }

    @GetMapping("/ajouter_louable")
    public String ajouterLouable(Model model) {
        model.addAttribute("typesLouable", TypeLouable.values());
        return "ajouter_louable";
    }

    @PostMapping("/ajouter_louable")
    public String ajouterLouablePost(
            @RequestParam TypeLouable type,

            // commun
            @RequestParam String prixJour,
            @RequestParam String lieuPrincipal,

            @RequestParam String marque,
            @RequestParam String modele,
            @RequestParam String annee,
            @RequestParam String couleur,
            @RequestParam String immatriculation,
            @RequestParam String kilometrage,

            // période optionnelle
            @RequestParam(required = false) String dispoDebut,
            @RequestParam(required = false) String dispoFin,

            // voiture
            @RequestParam(required = false) String nbPortes,
            @RequestParam(required = false) String nbPlaces,
            @RequestParam(required = false) String volumeCoffreLitres,
            @RequestParam(required = false) String boite,
            @RequestParam(required = false) String carburant,
            @RequestParam(required = false) String climatisation,

            // moto
            @RequestParam(required = false) String cylindreeCc,
            @RequestParam(required = false) String puissanceCh,
            @RequestParam(required = false) String typeMoto,
            @RequestParam(required = false) String permisRequisMoto,

            // camion
            @RequestParam(required = false) String chargeMaxKg,
            @RequestParam(required = false) String volumeUtileM3,
            @RequestParam(required = false) String hauteurM,
            @RequestParam(required = false) String longueurM,
            @RequestParam(required = false) String permisRequisCamion,

            Model modelView
    ) {
        modelView.addAttribute("typesLouable", TypeLouable.values());
        modelView.addAttribute("selectedType", type);

        try {
            if (blank(prixJour) || blank(lieuPrincipal) || blank(marque)
                    || blank(modele) || blank(annee) || blank(couleur) || blank(immatriculation) || blank(kilometrage)) {
                modelView.addAttribute("error", "Tous les champs communs sont obligatoires.");
                return "ajouter_louable";
            }

            Double prix = toDouble(prixJour, "prixJour");
            Integer an = toInt(annee, "annee");
            Integer km = toInt(kilometrage, "kilometrage");

            // IMPORTANT: on ne demande plus DISPONIBLE/INDISPONIBLE
            // Mets un statut métier stable (adapte si ton enum diffère)
            StatutLouable statutEnum = StatutLouable.EN_LOCATION;

            int idAgent = connexionService.getConnexion().getIdUtilisateur();

            int newId;

            switch (type) {
                case VOITURE -> {
                    if (blank(nbPortes) || blank(nbPlaces) || blank(volumeCoffreLitres) || blank(boite) || blank(carburant)) {
                        modelView.addAttribute("error", "Champs spécifiques Voiture obligatoires manquants.");
                        return "ajouter_louable";
                    }

                    Integer portes = toInt(nbPortes, "nbPortes");
                    Integer places = toInt(nbPlaces, "nbPlaces");
                    Integer coffre = toInt(volumeCoffreLitres, "volumeCoffreLitres");
                    Boolean climN = toBooleanNullable(climatisation, "climatisation");
                    boolean clim = climN != null && climN;

                    TypeBoite typeBoite = TypeBoite.valueOf(boite.trim().toUpperCase());
                    Carburant typeCarburant = Carburant.valueOf(carburant.trim().toUpperCase());

                    newId = voitureRepository.add(new Voiture(
                            idAgent, prix, statutEnum, lieuPrincipal,
                            marque, modele, an, couleur, immatriculation, km,
                            portes, places, coffre, typeBoite, typeCarburant, clim
                    ));
                }

                case MOTO -> {
                    if (blank(cylindreeCc) || blank(puissanceCh) || blank(typeMoto) || blank(permisRequisMoto)) {
                        modelView.addAttribute("error", "Champs spécifiques Moto obligatoires manquants.");
                        return "ajouter_louable";
                    }

                    Integer cc = toInt(cylindreeCc, "cylindreeCc");
                    Integer ch = toInt(puissanceCh, "puissanceCh");
                    TypeMoto tm = TypeMoto.valueOf(typeMoto.trim().toUpperCase());

                    newId = motoRepository.add(new Moto(
                            idAgent, prix, statutEnum, lieuPrincipal,
                            marque, modele, an, couleur, immatriculation, km,
                            cc, ch, tm, permisRequisMoto
                    ));
                }

                case CAMION -> {
                    if (blank(chargeMaxKg) || blank(volumeUtileM3) || blank(hauteurM) || blank(longueurM) || blank(permisRequisCamion)) {
                        modelView.addAttribute("error", "Champs spécifiques Camion obligatoires manquants.");
                        return "ajouter_louable";
                    }

                    Integer charge = toInt(chargeMaxKg, "chargeMaxKg");
                    Double vol = toDouble(volumeUtileM3, "volumeUtileM3");
                    Double h = toDouble(hauteurM, "hauteurM");
                    Double l = toDouble(longueurM, "longueurM");

                    newId = camionRepository.add(new Camion(
                            idAgent, prix, statutEnum, lieuPrincipal,
                            marque, modele, an, couleur, immatriculation, km,
                            charge, vol, h, l, permisRequisCamion
                    ));
                }

                default -> throw new IllegalArgumentException("Type inconnu: " + type);
            }

            // période optionnelle à la création
            if (!blank(dispoDebut) && !blank(dispoFin)) {
                LocalDate d1 = LocalDate.parse(dispoDebut);
                LocalDate d2 = LocalDate.parse(dispoFin);
                disponibiliteService.addOrMergeNonReservedRange(newId, d1, d2);
            }

            return "redirect:/louables/" + newId + "?succes=Louable%20ajout%C3%A9";

        } catch (Exception ex) {
            ex.printStackTrace();
            modelView.addAttribute("error", "Erreur lors de l'ajout: " + ex.getMessage());
            return "ajouter_louable";
        }
    }
}