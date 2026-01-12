package com.delorent.controller;

import com.delorent.model.Louable.*;

import com.delorent.repository.LouableRepository.VoitureRepository;
import com.delorent.repository.LouableRepository.MotoRepository;
import com.delorent.repository.LouableRepository.CamionRepository;

import com.delorent.service.ConnexionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AjouterLouableController {

    private final VoitureRepository voitureRepository;
    private final MotoRepository motoRepository;
    private final CamionRepository camionRepository;
    private final ConnexionService connexionService;

    public AjouterLouableController(
            VoitureRepository voitureRepository,
            MotoRepository motoRepository,
            CamionRepository camionRepository,
            ConnexionService connexionService
    ) {
        this.voitureRepository = voitureRepository;
        this.motoRepository = motoRepository;
        this.camionRepository = camionRepository;
        this.connexionService = connexionService;
    }

    private boolean blank(String s) {
        return s == null || s.isBlank();
    }

    private Integer toInt(String s, String field) {
        if (blank(s)) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Champ '" + field + "' invalide (entier attendu).");
        }
    }

    private Long toLong(String s, String field) {
        if (blank(s)) return null;
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Champ '" + field + "' invalide (entier long attendu).");
        }
    }

    private Double toDouble(String s, String field) {
        if (blank(s)) return null;
        try {
            return Double.parseDouble(s.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Champ '" + field + "' invalide (nombre attendu).");
        }
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
            // type
            @RequestParam TypeLouable type,

            // commun (Louable + Vehicule)
            @RequestParam String prixJour,
            @RequestParam String statut,
            @RequestParam String lieuPrincipal,

            @RequestParam String marque,
            @RequestParam String modele,
            @RequestParam String annee,
            @RequestParam String couleur,
            @RequestParam String immatriculation,
            @RequestParam String kilometrage,

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

        // (optionnel) rebind des valeurs pour réaffichage si erreur
        modelView.addAttribute("prixJour", prixJour);
        modelView.addAttribute("statut", statut);
        modelView.addAttribute("lieuPrincipal", lieuPrincipal);
        modelView.addAttribute("marque", marque);
        modelView.addAttribute("modele", modele);
        modelView.addAttribute("annee", annee);
        modelView.addAttribute("couleur", couleur);
        modelView.addAttribute("immatriculation", immatriculation);
        modelView.addAttribute("kilometrage", kilometrage);

        try {
            // validations de base
            if (blank(prixJour) || blank(statut) || blank(lieuPrincipal) || blank(marque)
                    || blank(modele) || blank(annee) || blank(couleur) || blank(immatriculation) || blank(kilometrage)) {
                modelView.addAttribute("error", "Tous les champs communs sont obligatoires.");
                return "ajouter_louable";
            }

            Double prix = toDouble(prixJour, "prixJour");
            Integer an = toInt(annee, "annee");
            Integer km = toInt(kilometrage, "kilometrage");

            StatutLouable statutEnum;
            try {
                statutEnum = StatutLouable.valueOf(statut.trim());
            } catch (Exception e) {
                throw new IllegalArgumentException("Champ 'statut' invalide.");
            }

            // SWITCH TYPE
            switch (type) {
                case VOITURE -> {
                    if (blank(nbPortes) || blank(nbPlaces) || blank(volumeCoffreLitres) || blank(boite) || blank(carburant)) {
                        modelView.addAttribute("error", "Champs spécifiques Voiture obligatoires manquants.");
                        return "ajouter_louable";
                    }

                    Integer portes = toInt(nbPortes, "nbPortes");
                    Integer places = toInt(nbPlaces, "nbPlaces");
                    Integer coffre = toInt(volumeCoffreLitres, "volumeCoffreLitres");
                    Boolean clim = toBooleanNullable(climatisation, "climatisation");
                    TypeBoite typeBoite;
                    try {
                        typeBoite = TypeBoite.valueOf(boite.trim().toUpperCase());
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Champ 'boite' invalide.");
                    }
                    Carburant typeCarburant;
                    try {
                        typeCarburant = Carburant.valueOf(carburant.trim().toUpperCase());
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Champ 'carburant' invalide.");
                    }

                    long newId = voitureRepository.add(new Voiture(connexionService.getConnexion().getIdUtilisateur(), prix, statutEnum, lieuPrincipal, marque, modele, an, couleur, immatriculation, km, portes, places, coffre, typeBoite, typeCarburant, clim));

                    modelView.addAttribute("message", "Voiture ajoutée (id=" + newId + ").");
                    return "resultat-ajouter-louable";
                }

                case MOTO -> {
                    if (blank(cylindreeCc) || blank(puissanceCh) || blank(typeMoto) || blank(permisRequisMoto)) {
                        modelView.addAttribute("error", "Champs spécifiques Moto obligatoires manquants.");
                        return "ajouter_louable";
                    }

                    Integer cc = toInt(cylindreeCc, "cylindreeCc");
                    Integer ch = toInt(puissanceCh, "puissanceCh");

                    long newId = motoRepository.add(new Moto(connexionService.getConnexion().getIdUtilisateur(), prix, statutEnum, lieuPrincipal, marque, modele, an, couleur, immatriculation, km, cc, ch, null, permisRequisMoto));

                    modelView.addAttribute("message", "Moto ajoutée (id=" + newId + ").");
                    return "resultat-ajouter-louable";
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

                    long newId = camionRepository.add(new Camion(connexionService.getConnexion().getIdUtilisateur(), prix, statutEnum, lieuPrincipal, marque, modele, an, couleur, immatriculation, km, charge, vol, h, l, permisRequisCamion));

                    modelView.addAttribute("message", "Camion ajouté (id=" + newId + ").");
                    return "resultat-ajouter-louable";
                }

                default -> throw new IllegalArgumentException("Type inconnu: " + type);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            modelView.addAttribute("error", "Erreur lors de l'ajout: " + ex.getMessage());
            return "ajouter_louable";
        }
    }
}
