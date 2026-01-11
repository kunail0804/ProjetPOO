package com.delorent.repository;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.Agent;
import com.delorent.model.Vehicule;

@Repository
public class AgentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AgentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Agent trouverAgentParId(int idAgent) {
        // CORRECTION : On a retiré "a.typeAgent" de la liste des colonnes
        String sql = "SELECT u.*, a.nom, a.prenom " +
                     "FROM UTILISATEUR u " +
                     "JOIN AGENT a ON u.idUtilisateur = a.idUtilisateur " +
                     "WHERE u.idUtilisateur = ?";
        
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Agent.class), idAgent);
    }

    public List<Vehicule> trouverVehiculesParAgent(int idAgent) {
        String sql = "SELECT l.id, l.marque, l.prixJour, l.lieuPrincipal, l.statut, " +
                     "v.modele, v.annee, v.kilometrage " +
                     "FROM LOUABLE l " +
                     "JOIN VEHICULE v ON l.id = v.id " +
                     "WHERE l.idProprietaire = ?";
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Vehicule.class), idAgent);
    }
}