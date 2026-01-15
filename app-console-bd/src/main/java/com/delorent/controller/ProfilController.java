package com.delorent.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Important pour le fichier

import com.delorent.model.*;
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.EntrepriseEntretien;
import com.delorent.model.Utilisateur.Loueur;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.repository.AssuranceRepository; // Import ajouté
import com.delorent.repository.ContratRepository;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.service.ConnexionService;
import com.delorent.service.UtilisateurService;

@Controller
public class ProfilController {

    private final ConnexionService connexionService;
    private final UtilisateurService utilisateurService;
    private final ContratRepository contratRepository;
    private final LouableRepository louableRepository;
    private final AssuranceRepository assuranceRepository; // On ajoute le repo

    // Dossier où seront stockés les fichiers (dans static/uploads)
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/uploads/assurances";

    public ProfilController(ConnexionService connexionService, 
                            UtilisateurService utilisateurService, 
                            ContratRepository contratRepository, 
                            LouableRepository louableRepository,
                            AssuranceRepository assuranceRepository) { // Constructeur mis à jour
        this.connexionService = connexionService;
        this.utilisateurService = utilisateurService;
        this.contratRepository = contratRepository;
        this.louableRepository = louableRepository;
        this.assuranceRepository = assuranceRepository;
    }

    @GetMapping("/profil")
    public String afficherProfil(Model model) {
        Utilisateur utilisateur = connexionService.getConnexion();
        if (utilisateur == null) return "redirect:/connexion";

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("nomComplet", buildNomComplet(utilisateur));
        model.addAttribute("initiale", buildInitiale(utilisateur));

        boolean isAgent = utilisateur instanceof Agent;
        model.addAttribute("isLoueur", utilisateur instanceof Loueur);
        model.addAttribute("isAgent", isAgent);
        model.addAttribute("isEntretien", utilisateur instanceof EntrepriseEntretien);

        if (utilisateur instanceof Loueur l) {
            model.addAttribute("contrats", contratRepository.getByLoueurId(l.getIdUtilisateur()));
        }

        if (utilisateur instanceof Agent a) {
            model.addAttribute("louables", louableRepository.getByProprietaire(a.getIdUtilisateur()));
            
            // NOUVEAU : On charge les assurances de l'agent pour les afficher
            model.addAttribute("mesAssurances", assuranceRepository.getByProprietaire(a.getIdUtilisateur()));
        }

        return "profil";
    }

    // --- NOUVELLE METHODE POUR UPLOAD ASSURANCE ---
    @PostMapping("/profil/assurance/ajouter")
    public String ajouterAssurance(@RequestParam("nom") String nom,
                                   @RequestParam("tarif") double tarif,
                                   @RequestParam("fichier") MultipartFile fichier) {
        
        Utilisateur utilisateur = connexionService.getConnexion();
        // Sécurité : seul un agent connecté peut faire ça
        if (utilisateur == null || !(utilisateur instanceof Agent)) {
            return "redirect:/connexion";
        }

        try {
            String cheminFichierEnBase = null;

            // 1. Sauvegarde du fichier si présent
            if (!fichier.isEmpty()) {
                // Créer le dossier s'il n'existe pas
                Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Générer un nom unique pour éviter les conflits (ex: mon_fichier_12345.pdf)
                String nomFichier = UUID.randomUUID().toString() + "_" + fichier.getOriginalFilename();
                Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, nomFichier);
                
                // Écriture sur le disque
                Files.write(fileNameAndPath, fichier.getBytes());

                // Chemin relatif à stocker en base (accessible via http://localhost:8080/uploads/assurances/...)
                cheminFichierEnBase = "/uploads/assurances/" + nomFichier;
            }

            // 2. Création de l'objet Assurance
            Assurance nouvelle = new Assurance(
                nom,
                tarif,
                cheminFichierEnBase,
                utilisateur.getIdUtilisateur() // ID du propriétaire (l'agent connecté)
            );

            // 3. Sauvegarde en BDD
            assuranceRepository.add(nouvelle);

        } catch (IOException e) {
            e.printStackTrace();
            // Idéalement, ajouter un message d'erreur dans le modèle
        }

        return "redirect:/profil";
    }

    // ... (Reste des méthodes: editionProfil, enregistrerEdition, supprimerCompte...)
    // ... (Gardez-les telles quelles, j'ai juste ajouté le code ci-dessus)
    
    // Garde bien tes méthodes buildNomComplet et buildInitiale en bas...
    
    // CODE ORIGINAL POUR LA COMPILATION :
    @GetMapping("/profil/edition")
    public String editionProfil(Model model) {
        Utilisateur utilisateur = connexionService.getConnexion();
        if (utilisateur == null) return "redirect:/connexion";
        ProfilEditionForm form = new ProfilEditionForm();
        form.setMail(utilisateur.getMail());
        form.setAdresse(utilisateur.getAdresse());
        form.setVille(utilisateur.getVille());
        form.setCodePostal(utilisateur.getCodePostal());
        form.setRegion(utilisateur.getRegion());
        form.setTelephone(utilisateur.getTelephone());
        if (utilisateur instanceof Loueur l) {
            form.setNom(l.getNom()); form.setPrenom(l.getPrenom());
        } else if (utilisateur instanceof Agent a) {
            form.setNom(a.getNom()); form.setPrenom(a.getPrenom());
        } else if (utilisateur instanceof EntrepriseEntretien e) {
            form.setNomEntreprise(e.getNomEntreprise()); form.setRaisonSoc(e.getRaisonSoc()); form.setNoSiret(e.getNoSiret());
        }
        model.addAttribute("form", form);
        model.addAttribute("isLoueur", utilisateur instanceof Loueur);
        model.addAttribute("isAgent", utilisateur instanceof Agent);
        model.addAttribute("isEntretien", utilisateur instanceof EntrepriseEntretien);
        return "profil_edition";
    }
    @PostMapping("/profil/edition")
    public String enregistrerEdition(@ModelAttribute("form") ProfilEditionForm form) {
        Utilisateur utilisateur = connexionService.getConnexion();
        if (utilisateur == null) return "redirect:/connexion";
        utilisateur.setMail(form.getMail());
        utilisateur.setAdresse(form.getAdresse());
        utilisateur.setVille(form.getVille());
        utilisateur.setCodePostal(form.getCodePostal());
        utilisateur.setRegion(form.getRegion());
        utilisateur.setTelephone(form.getTelephone());
        if (form.getMotDePasse() != null && !form.getMotDePasse().isBlank()) utilisateur.setMotDePasse(form.getMotDePasse());
        if (utilisateur instanceof Loueur l) { l.setNom(form.getNom()); l.setPrenom(form.getPrenom()); utilisateurService.updateLoueur(l); }
        else if (utilisateur instanceof Agent a) { a.setNom(form.getNom()); a.setPrenom(form.getPrenom()); utilisateurService.updateAgent(a); }
        else if (utilisateur instanceof EntrepriseEntretien e) { e.setNomEntreprise(form.getNomEntreprise()); e.setRaisonSoc(form.getRaisonSoc()); e.setNoSiret(form.getNoSiret()); utilisateurService.updateEntrepriseEntretien(e); }
        connexionService.refreshConnexion((long) utilisateur.getIdUtilisateur());
        return "redirect:/profil";
    }
    @PostMapping("/profil/suppression")
    public String supprimerCompte() {
        Utilisateur utilisateur = connexionService.getConnexion();
        if (utilisateur == null) return "redirect:/";
        if(utilisateur instanceof Loueur l) utilisateurService.supprimerLoueur((long) l.getIdUtilisateur());
        else if (utilisateur instanceof Agent a) utilisateurService.supprimerAgent((long) a.getIdUtilisateur());
        else if (utilisateur instanceof EntrepriseEntretien e) utilisateurService.supprimerEntrepriseEntretien((long) e.getIdUtilisateur());
        connexionService.deconnecter();
        return "redirect:/";
    }
    private String buildNomComplet(Utilisateur u) {
        if (u instanceof Loueur l) return l.getPrenom() + " " + l.getNom();
        if (u instanceof Agent a) return a.getPrenom() + " " + a.getNom();
        if (u instanceof EntrepriseEntretien e) return e.getNomEntreprise();
        return "Utilisateur";
    }
    private String buildInitiale(Utilisateur u) {
        if (u instanceof Loueur l && l.getPrenom() != null) return l.getPrenom().substring(0, 1).toUpperCase();
        if (u instanceof Agent a && a.getPrenom() != null) return a.getPrenom().substring(0, 1).toUpperCase();
        if (u instanceof EntrepriseEntretien e && e.getNomEntreprise() != null) return e.getNomEntreprise().substring(0, 1).toUpperCase();
        return "U";
    }
}