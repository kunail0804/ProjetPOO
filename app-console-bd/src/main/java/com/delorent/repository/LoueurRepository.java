package com.delorent.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.Loueur;

@Repository
public class LoueurRepository {

    private final JdbcTemplate jdbcTemplate;

    public LoueurRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Loueur trouverParId(int idUtilisateur) {
        // JOINTURE : On prend tout de UTILISATEUR + tout de LOUEUR
        String sql = "SELECT u.*, l.nom, l.prenom " +
                     "FROM UTILISATEUR u " +
                     "JOIN LOUEUR l ON u.idUtilisateur = l.idUtilisateur " +
                     "WHERE u.idUtilisateur = ?";
        
        // Spring va remplir automatiquement les champs hérités (mail, ville) ET les champs Loueur (nom, prenom)
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Loueur.class), idUtilisateur);
    }
}