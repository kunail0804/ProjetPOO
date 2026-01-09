package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ContratRepository {

    private final JdbcTemplate jdbc;

    public ContratRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllContratsForHome() {
        return jdbc.queryForList(
                """
                SELECT c.idContrat,
                       c.dateDebut,
                       c.dateFin,
                       c.lieuPrise,
                       c.lieuDepot,
                       c.idLoueur,
                       CONCAT(l.prenom, ' ', l.nom) AS loueurNom,
                       c.idLouable,
                       CONCAT(lo.marque, ' ', COALESCE(v.modele,''), ' (', COALESCE(v.immatriculation,''), ')') AS vehicule,
                       c.idAssurance,
                       a.nom AS assuranceNom
                FROM CONTRAT c
                LEFT JOIN LOUEUR l ON l.idUtilisateur = c.idLoueur
                LEFT JOIN LOUABLE lo ON lo.id = c.idLouable
                LEFT JOIN VEHICULE v ON v.id = c.idLouable
                LEFT JOIN ASSURANCE a ON a.idAssurance = c.idAssurance
                ORDER BY c.idContrat DESC
                """
        );
    }
}