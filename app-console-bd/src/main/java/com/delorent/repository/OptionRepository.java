package com.delorent.repository;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.OptionPayante;

@Repository
public class OptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public OptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OptionPayante> trouverOptionsPourAgent(int idAgent) {
        // CORRECTION : On utilise les noms camelCase de la nouvelle BDD
        // Et on sélectionne bien estActive et dateSouscription pour le HTML
        String sql = "SELECT " +
                     "o.idOption, " +
                     "o.nomOption, " +
                     "o.description, " +
                     "o.prixMensuel, " + 
                     "s.estActive, " +
                     "s.dateSouscription " +
                     "FROM OPTION_PAYANTE o " +
                     "LEFT JOIN SOUSCRIT s ON o.idOption = s.idOption AND s.idAgent = ?";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(OptionPayante.class), idAgent);
    }

    public void souscrire(int idAgent, int idOption) {
        // CORRECTION : idAgent (pas id_agent)
        String sql = "INSERT INTO SOUSCRIT (idAgent, idOption, dateSouscription, estActive) " +
                     "VALUES (?, ?, NOW(), 1) " +
                     "ON DUPLICATE KEY UPDATE estActive = 1, dateSouscription = NOW()";
        
        jdbcTemplate.update(sql, idAgent, idOption);
    }

    public void resilier(int idAgent, int idOption) {
        // CORRECTION : idAgent et estActive (pas id_agent et est_active)
        String sql = "UPDATE SOUSCRIT SET estActive = 0 WHERE idAgent = ? AND idOption = ?";
        jdbcTemplate.update(sql, idAgent, idOption);
    }
}