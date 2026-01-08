package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class LocationRepository {

    private final JdbcTemplate jdbc;

    public LocationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> listerLoueurs() {
        return jdbc.queryForList("""
            SELECT u.id_utilisateur, u.prenom, u.nom, u.email
            FROM UTILISATEUR u
            JOIN LOUEUR l ON l.id_utilisateur = u.id_utilisateur
            ORDER BY u.prenom, u.nom
        """);
    }

    public List<Map<String, Object>> listerLouables() {
        return jdbc.queryForList("""
            SELECT lo.idLouable,
                   v.marque, v.modele, v.immatriculation,
                   lo.prixJour, lo.statut, lo.lieuPrincipal
            FROM LOUABLE lo
            JOIN VEHICULE v ON v.id_louable = lo.idLouable
            ORDER BY lo.idLouable DESC
        """);
    }

    public List<Map<String, Object>> listerAssurances() {
        return jdbc.queryForList("""
            SELECT id_assurance, nom_assurance, prix_journalier
            FROM ASSURANCE
            ORDER BY nom_assurance
        """);
    }

    public List<Map<String, Object>> listerContrats() {
        return jdbc.queryForList("""
            SELECT c.id_contrat,
                   c.date_debut,
                   c.date_fin,
                   c.etat,
                   c.prix_final,
                   c.lieu_prise,
                   c.lieu_depot,

                   ul.id_utilisateur AS id_loueur,
                   ul.prenom AS prenom_loueur,
                   ul.nom AS nom_loueur,

                   v.id_louable,
                   v.marque,
                   v.modele,
                   v.immatriculation,

                   a.id_assurance,
                   a.nom_assurance

            FROM CONTRAT c
            JOIN UTILISATEUR ul ON ul.id_utilisateur = c.id_loueur
            JOIN VEHICULE v ON v.id_louable = c.id_louable
            JOIN ASSURANCE a ON a.id_assurance = c.id_assurance
            ORDER BY c.id_contrat DESC
        """);
    }
}