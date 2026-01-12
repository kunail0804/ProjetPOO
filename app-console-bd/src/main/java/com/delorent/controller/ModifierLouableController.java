package com.delorent.controller;

import com.delorent.model.Louable.*;
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Utilisateur;

import com.delorent.repository.LouableRepository.VoitureRepository;
import com.delorent.repository.LouableRepository.MotoRepository;
import com.delorent.repository.LouableRepository.CamionRepository;

import com.delorent.service.ConnexionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ModifierLouableController {

    private final VoitureRepository voitureRepository;
    private final MotoRepository motoRepository;
    private final CamionRepository camionRepository;
    private final ConnexionService connexionService;

    public ModifierLouableController(
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

    private boolean blank(String s) { return s == null || s.isBlank(); }

    private int toInt(String s, String field) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Champ '" + field + "' invalide."); }
    }

    private double toDouble(String s, String field) {
        try { return Double.parseDouble(s.trim().replace(',', '.')); }
        catch (Exception e) { throw new IllegalArgumentException("Champ '" + field + "' invalide."); }
    }

    private Boolean toBooleanNullable(String s, String field) {
        if (blank(s)) return null;
        if ("true".equalsIgnoreCase(s.trim())) return true;
        if ("false".equalsIgnoreCase(s.trim())) return false;
        throw new IllegalArgumentException("Champ '" + field + "' invalide (true/false attendu).");
    }

    private TypeBoite parseBoite(String s) {
        try { return TypeBoite.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { throw new IllegalArgumentException("Champ 'boite' invalide."); }
    }

    private Carburant parseCarburant(String s) {
        try { return Carburant.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { throw new IllegalArgumentException("Champ 'carburant' invalide."); }
    }

    private StatutLouable parseStatut(String s) {
        try { return StatutLouable.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { throw new IllegalArgumentException("Champ 'statut' invalide."); }
    }

    private TypeMoto parseTypeMoto(String s) {
        try { return TypeMoto.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { throw new IllegalArgumentException("Champ 'typeMoto' invalide."); }
    }

    private void guardAgentOwner(Object louable) {
        Utilisateur u = connexionService.getConnexion();
        if (u == null) throw new IllegalArgumentException("Vous devez être connecté.");
        if (!(u instanceof Agent)) throw new IllegalArgumentException("Accès réservé aux agents.");

        int idUser = u.getIdUtilisateur();

        // ⚠️ on part du principe que Vehicule contient getIdAgent()
        if (louable instanceof Vehicule v) {
            if (v.getIdAgent() != idUser) throw new IllegalArgumentException("Ce louable ne vous appartient pas.");
        } else {
            throw new IllegalArgumentException("Type de louable invalide.");
        }
    }

    @GetMapping("/louables/{id}/modifier")
    public String modifierLouable(@PathVariable("id") int id, Model model) {

        // On tente dans l’ordre: voiture -> moto -> camion
        // (si tu as un "type" en DB, c’est mieux, mais là on fait simple)
        Object louable = voitureRepository.get(id);
        String type = "VOITURE";

        if (louable == null) {
            louable = motoRepository.get(id);
            type = "MOTO";
        }
        if (louable == null) {
            louable = camionRepository.get(id);
            type = "CAMION";
        }

        if (louable == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + id + ").");
            return "modifier_louable";
        }

        // sécurité : agent + propriétaire
        try {
            guardAgentOwner(louable);
        } catch (Exception e) {
            model.addAttribute("erreur", e.getMessage());
            return "modifier_louable";
        }

        model.addAttribute("idLouable", id);
        model.addAttribute("type", type);
        model.addAttribute("louable", louable);

        // flags pour afficher le bon bloc
        model.addAttribute("isVoiture", "VOITURE".equals(type));
        model.addAttribute("isMoto", "MOTO".equals(type));
        model.addAttribute("isCamion", "CAMION".equals(type));

        return "modifier_louable";
    }

    @PostMapping("/louables/{id}/modifier")
    public String enregistrerModification(
            @PathVariable("id") int id,

            // commun
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

            Model model
    ) {
        // Rechargement du louable complet pour connaître son type + sécurité
        Object existing = voitureRepository.get(id);
        String type = "VOITURE";
        if (existing == null) { existing = motoRepository.get(id); type = "MOTO"; }
        if (existing == null) { existing = camionRepository.get(id); type = "CAMION"; }

        if (existing == null) {
            model.addAttribute("erreur", "Louable introuvable (id=" + id + ").");
            return "modifier_louable";
        }

        // sécurité
        try {
            guardAgentOwner(existing);
        } catch (Exception e) {
            model.addAttribute("erreur", e.getMessage());
            return "modifier_louable";
        }

        try {
            // validations de base
            if (blank(prixJour) || blank(statut) || blank(lieuPrincipal) || blank(marque) || blank(modele)
                    || blank(annee) || blank(couleur) || blank(immatriculation) || blank(kilometrage)) {
                throw new IllegalArgumentException("Tous les champs communs sont obligatoires.");
            }

            double prix = toDouble(prixJour, "prixJour");
            int an = toInt(annee, "annee");
            int km = toInt(kilometrage, "kilometrage");
            StatutLouable statutEnum = parseStatut(statut);

            int idAgent = connexionService.getConnexion().getIdUtilisateur();

            switch (type) {
                case "VOITURE" -> {
                    if (blank(nbPortes) || blank(nbPlaces) || blank(volumeCoffreLitres) || blank(boite) || blank(carburant)) {
                        throw new IllegalArgumentException("Champs spécifiques Voiture obligatoires manquants.");
                    }

                    int portes = toInt(nbPortes, "nbPortes");
                    int places = toInt(nbPlaces, "nbPlaces");
                    int coffre = toInt(volumeCoffreLitres, "volumeCoffreLitres");
                    TypeBoite b = parseBoite(boite);
                    Carburant carb = parseCarburant(carburant);

                    // Ton Voiture a climatisation boolean (pas nullable)
                    Boolean climN = toBooleanNullable(climatisation, "climatisation");
                    boolean clim = (climN != null) ? climN : false;

                    Voiture v = new Voiture(id, idAgent, prix, statutEnum, lieuPrincipal,
                            marque, modele, an, couleur, immatriculation, km,
                            portes, places, coffre, b, carb, clim);

                    // ⚠️ adapte le nom : update/modify
                    voitureRepository.modify(v);
                }

                case "MOTO" -> {
                    if (blank(cylindreeCc) || blank(puissanceCh) || blank(typeMoto) || blank(permisRequisMoto)) {
                        throw new IllegalArgumentException("Champs spécifiques Moto obligatoires manquants.");
                    }

                    int cc = toInt(cylindreeCc, "cylindreeCc");
                    int ch = toInt(puissanceCh, "puissanceCh");
                    TypeMoto tm = parseTypeMoto(typeMoto);

                    Moto m = new Moto(id, idAgent, prix, statutEnum, lieuPrincipal,
                            marque, modele, an, couleur, immatriculation, km,
                            cc, ch, tm, permisRequisMoto);

                    motoRepository.modify(m);
                }

                case "CAMION" -> {
                    if (blank(chargeMaxKg) || blank(volumeUtileM3) || blank(hauteurM) || blank(longueurM) || blank(permisRequisCamion)) {
                        throw new IllegalArgumentException("Champs spécifiques Camion obligatoires manquants.");
                    }

                    int charge = toInt(chargeMaxKg, "chargeMaxKg");
                    double vol = toDouble(volumeUtileM3, "volumeUtileM3");
                    double h = toDouble(hauteurM, "hauteurM");
                    double l = toDouble(longueurM, "longueurM");

                    Camion c = new Camion(id, idAgent, prix, statutEnum, lieuPrincipal,
                            marque, modele, an, couleur, immatriculation, km,
                            charge, vol, h, l, permisRequisCamion);

                    camionRepository.modify(c);
                }

                default -> throw new IllegalArgumentException("Type inconnu: " + type);
            }

            return "redirect:/louables/" + id + "?succes=Modification%20enregistr%C3%A9e";

        } catch (Exception ex) {
            ex.printStackTrace();

            // réaffichage page avec erreur + valeurs
            model.addAttribute("erreur", ex.getMessage());
            model.addAttribute("idLouable", id);
            model.addAttribute("type", type);
            model.addAttribute("isVoiture", "VOITURE".equals(type));
            model.addAttribute("isMoto", "MOTO".equals(type));
            model.addAttribute("isCamion", "CAMION".equals(type));

            // on peut aussi remettre un "louable" reconstruit, mais au minimum on peut recharger l'existant
            model.addAttribute("louable", existing);

            return "modifier_louable";
        }
    }
}
