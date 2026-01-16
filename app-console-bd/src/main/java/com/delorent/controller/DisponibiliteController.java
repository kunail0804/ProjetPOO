package com.delorent.controller;

import com.delorent.model.Louable.Disponibilite;
import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.repository.DisponibiliteRepository;
import com.delorent.service.ConnexionService;
import com.delorent.service.DisponibiliteService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DisponibiliteController {

    private final ConnexionService connexionService;
    private final DisponibiliteRepository dispoRepo;
    private final DisponibiliteService dispoService;

    public DisponibiliteController(ConnexionService connexionService,
                                  DisponibiliteRepository dispoRepo,
                                  DisponibiliteService dispoService) {
        this.connexionService = connexionService;
        this.dispoRepo = dispoRepo;
        this.dispoService = dispoService;
    }

    private void guardAgentOwner(int idLouable) {
        Utilisateur u = connexionService.getConnexion();
        if (u == null) throw new IllegalArgumentException("Vous devez être connecté.");
        if (!(u instanceof Agent)) throw new IllegalArgumentException("Accès réservé aux agents.");

        Integer owner = dispoRepo.getProprietaireIdForLouable(idLouable);
        if (owner == null) throw new IllegalArgumentException("Louable introuvable.");
        if (owner != u.getIdUtilisateur()) throw new IllegalArgumentException("Ce louable ne vous appartient pas.");
    }

    @GetMapping(value = "/louer/disponibilites", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Disponibilite> disponibilitesJson(@RequestParam("idLouable") int idLouable) {
        return dispoService.getByLouable(idLouable);
    }

    @PostMapping("/louables/{idLouable}/disponibilites/ajouter")
    public String ajouterDispoAgent(@PathVariable int idLouable,
                                    @RequestParam("dateDebut") String dateDebut,
                                    @RequestParam("dateFin") String dateFin) {

        guardAgentOwner(idLouable);

        LocalDate d1 = LocalDate.parse(dateDebut);
        LocalDate d2 = LocalDate.parse(dateFin);

        dispoService.addOrMergeNonReservedRange(idLouable, d1, d2);

        return "redirect:/louables/" + idLouable + "?succes=Disponibilit%C3%A9%20ajout%C3%A9e";
    }

    @PostMapping("/louables/{idLouable}/disponibilites/supprimer")
    public String supprimerDispoAgent(@PathVariable int idLouable,
                                     @RequestParam("idDisponibilite") int idDisponibilite) {

        guardAgentOwner(idLouable);

        dispoService.deleteRangeIfNoContrat(idLouable, idDisponibilite);

        return "redirect:/louables/" + idLouable + "?succes=Disponibilit%C3%A9%20supprim%C3%A9e";
    }
}