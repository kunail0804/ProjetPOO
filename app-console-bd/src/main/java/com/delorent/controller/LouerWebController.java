package com.delorent.controller;

import com.delorent.model.Contrat;
import com.delorent.service.ServiceLocation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class LouerWebController {

    private final JdbcTemplate jdbc;
    private final ServiceLocation serviceLocation;

    public LouerWebController(JdbcTemplate jdbc, ServiceLocation serviceLocation) {
        this.jdbc = jdbc;
        this.serviceLocation = serviceLocation;
    }

    @GetMapping("/louer")
    public String pageLouer(Model model) {
        chargerListes(model);
        return "louer";
    }

    @PostMapping("/louer")
    public String louer(@RequestParam int idLoueur,
                        @RequestParam int idLouable,
                        @RequestParam int idAssurance,
                        @RequestParam LocalDate dateDebut,
                        @RequestParam LocalDate dateFin,
                        @RequestParam(required = false) String lieuDepotOptionnel,
                        Model model) {

        chargerListes(model);

        try {
            Contrat contrat = serviceLocation.louer(idLoueur, idLouable, idAssurance, dateDebut, dateFin, lieuDepotOptionnel);
            model.addAttribute("contrat", contrat);
            model.addAttribute("succes", "Location créée. Prix estimé: " + contrat.getPrixEstime() + "€");
        } catch (Exception e) {
            model.addAttribute("erreur", rootMessage(e));
        }

        return "louer";
    }

    @GetMapping("/louer/disponibilites")
    @ResponseBody
    public List<Map<String, Object>> disponibilites(@RequestParam int idLouable) {
        return jdbc.queryForList(
                """
                SELECT idDisponibilite, dateDebut, dateFin, estReservee
                FROM DISPONIBILITE
                WHERE idLouable = ?
                ORDER BY dateDebut ASC
                """,
                idLouable
        );
    }

    private void chargerListes(Model model) {
        List<Map<String, Object>> loueurs = jdbc.queryForList(
                "SELECT idUtilisateur, nom, prenom FROM LOUEUR ORDER BY prenom, nom"
        );

        List<Map<String, Object>> louables = jdbc.queryForList(
                """
                SELECT l.id AS idLouable,
                       l.marque,
                       l.prixJour,
                       l.statut,
                       l.lieuPrincipal,
                       v.modele,
                       v.immatriculation
                FROM LOUABLE l
                LEFT JOIN VEHICULE v ON v.id = l.id
                ORDER BY l.id
                """
        );

        List<Map<String, Object>> assurances = jdbc.queryForList(
                "SELECT idAssurance, nom, tarifJournalier FROM ASSURANCE ORDER BY idAssurance"
        );

        model.addAttribute("loueurs", loueurs == null ? Collections.emptyList() : loueurs);
        model.addAttribute("louables", louables == null ? Collections.emptyList() : louables);
        model.addAttribute("assurances", assurances == null ? Collections.emptyList() : assurances);
    }

    private static String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage() == null ? cur.getClass().getSimpleName() : cur.getMessage();
    }
}