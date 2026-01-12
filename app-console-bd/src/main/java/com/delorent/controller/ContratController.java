package com.delorent.controller;

import com.delorent.config.UploadProperties;
import com.delorent.model.Utilisateur.Loueur;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.ReleveKilometrageRepository;
import com.delorent.service.ConnexionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/contrats")
public class ContratController {

    private final ConnexionService connexionService;
    private final ContratRepository contratRepository;
    private final ReleveKilometrageRepository releveRepository;
    private final UploadProperties uploadProperties;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    public ContratController(ConnexionService connexionService,
                             ContratRepository contratRepository,
                             ReleveKilometrageRepository releveRepository,
                             UploadProperties uploadProperties) {
        this.connexionService = connexionService;
        this.contratRepository = contratRepository;
        this.releveRepository = releveRepository;
        this.uploadProperties = uploadProperties;
    }

    private Loueur requireLoueur() {
        Utilisateur u = connexionService.getConnexion();
        if (u == null) throw new IllegalStateException("Non connecté");
        if (u instanceof Loueur l) return l;
        throw new IllegalStateException("Accès réservé aux loueurs");
    }

    @GetMapping("/{idContrat}")
    public String detail(@PathVariable int idContrat, Model model) {
        Loueur loueur;
        try {
            loueur = requireLoueur();
        } catch (Exception e) {
            return "redirect:/connexion";
        }

        var contrat = contratRepository.getDetailByIdAndLoueur(idContrat, loueur.getIdUtilisateur());
        if (contrat == null) {
            model.addAttribute("erreur", "Contrat introuvable ou non autorisé.");
            return "contrat-detail";
        }

        var prise = releveRepository.findByContratAndType(idContrat, "PRISE");
        var retour = releveRepository.findByContratAndType(idContrat, "RETOUR");

        model.addAttribute("contrat", contrat);
        model.addAttribute("relevePrise", prise);
        model.addAttribute("releveRetour", retour);
        return "contrat-detail";
    }

    @PostMapping("/{idContrat}/releves")
    public String enregistrerReleve(@PathVariable int idContrat,
                                    @RequestParam String typeReleve,
                                    @RequestParam int kilometrage,
                                    @RequestParam("photo") MultipartFile photo,
                                    RedirectAttributes ra) throws IOException {

        Loueur loueur = requireLoueur();

        var contrat = contratRepository.getDetailByIdAndLoueur(idContrat, loueur.getIdUtilisateur());
        if (contrat == null) {
            ra.addFlashAttribute("erreur", "Contrat introuvable ou non autorisé.");
            return "redirect:/contrats/" + idContrat;
        }

        if (!"PRISE".equals(typeReleve) && !"RETOUR".equals(typeReleve)) {
            ra.addFlashAttribute("erreur", "Type de relevé invalide.");
            return "redirect:/contrats/" + idContrat;
        }

        if (kilometrage <= 0) {
            ra.addFlashAttribute("erreur", "Kilométrage invalide.");
            return "redirect:/contrats/" + idContrat;
        }

        if (photo == null || photo.isEmpty()) {
            ra.addFlashAttribute("erreur", "Photo obligatoire.");
            return "redirect:/contrats/" + idContrat;
        }

        if (releveRepository.findByContratAndType(idContrat, typeReleve) != null) {
            ra.addFlashAttribute("erreur", "Relevé déjà saisi pour " + typeReleve + ".");
            return "redirect:/contrats/" + idContrat;
        }

        if ("RETOUR".equals(typeReleve)) {
            var prise = releveRepository.findByContratAndType(idContrat, "PRISE");
            if (prise == null) {
                ra.addFlashAttribute("erreur", "Tu dois saisir le relevé de PRISE avant le RETOUR.");
                return "redirect:/contrats/" + idContrat;
            }
            if (kilometrage < prise.kilometrage()) {
                ra.addFlashAttribute("erreur", "Le kilométrage de retour ne peut pas être inférieur à la prise.");
                return "redirect:/contrats/" + idContrat;
            }
        }

        String original = StringUtils.cleanPath(photo.getOriginalFilename() == null ? "" : photo.getOriginalFilename());
        String ext = getExtLower(original);
        if (!ALLOWED_EXT.contains(ext)) {
            ra.addFlashAttribute("erreur", "Format photo non autorisé (jpg, png, webp).");
            return "redirect:/contrats/" + idContrat;
        }

        Path root = uploadProperties.getUploadRoot();
        Path dir = root.resolve("releves");
        Files.createDirectories(dir);

        String filename = UUID.randomUUID() + "." + ext;
        Path target = dir.resolve(filename);
        Files.copy(photo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String photoPath = "/uploads/releves/" + filename;
        releveRepository.insert(idContrat, typeReleve, kilometrage, photoPath);

        ra.addFlashAttribute("succes", "Relevé " + typeReleve + " enregistré.");
        return "redirect:/contrats/" + idContrat;
    }

    private String getExtLower(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx < 0 ? "" : filename.substring(idx + 1).toLowerCase();
    }
}