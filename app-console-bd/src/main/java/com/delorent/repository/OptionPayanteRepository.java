package com.delorent.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.OptionPayante;

@Repository
public class OptionPayanteRepository {

    private final JdbcTemplate jdbc;

    public OptionPayanteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Liste toutes les options + indique si l'agent les a souscrites et si elles sont actives.
     */
    public List<OptionPayante> findAllWithAgentStatus(int idAgent) {
        String sql = """
            SELECT o.id_option, o.nom_option, o.prix_mensuel, o.description,
                   CASE WHEN s.est_active = 1 THEN 1 ELSE 0 END AS active_pour_agent
            FROM OPTION_PAYANTE o
            LEFT JOIN (
                SELECT id_option, est_active
                FROM SOUSCRIT
                WHERE id_agent = ?
                ORDER BY date_souscription DESC
            ) s ON s.id_option = o.id_option
            GROUP BY o.id_option, o.nom_option, o.prix_mensuel, o.description, active_pour_agent
            ORDER BY o.nom_option
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            OptionPayante opt = new OptionPayante();
            opt.setIdOption(rs.getInt("id_option"));
            opt.setNomOption(rs.getString("nom_option"));
            opt.setPrixMensuel(rs.getFloat("prix_mensuel"));
            opt.setDescription(rs.getString("description"));
            opt.setActivePourAgent(rs.getInt("active_pour_agent") == 1);
            return opt;
        }, idAgent);
    }

    /**
     * Contracter = si déjà souscrit -> activer, sinon insérer une nouvelle souscription.
     */
    public void contracter(int idAgent, int idOption) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM SOUSCRIT WHERE id_agent=? AND id_option=?",
                Integer.class, idAgent, idOption
        );

        if (count != null && count > 0) {
            jdbc.update("UPDATE SOUSCRIT SET est_active=1 WHERE id_agent=? AND id_option=?",
                    idAgent, idOption);
        } else {
            jdbc.update("INSERT INTO SOUSCRIT(id_agent, id_option, est_active) VALUES (?,?,1)",
                    idAgent, idOption);
        }
    }

    /**
     * Annuler = passer est_active à 0 (on garde l'historique)
     */
    public void annuler(int idAgent, int idOption) {
        jdbc.update("UPDATE SOUSCRIT SET est_active=0 WHERE id_agent=? AND id_option=?",
                idAgent, idOption);
    }

    /**
     * Côté loueur : options actives pour un agent (à afficher pendant la location)
     */
    public List<OptionPayante> findActiveOptionsForAgent(int idAgent) {
        String sql = """
            SELECT o.id_option, o.nom_option, o.prix_mensuel, o.description
            FROM OPTION_PAYANTE o
            JOIN SOUSCRIT s ON s.id_option = o.id_option
            WHERE s.id_agent = ? AND s.est_active = 1
            ORDER BY o.nom_option
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            OptionPayante opt = new OptionPayante();
            opt.setIdOption(rs.getInt("id_option"));
            opt.setNomOption(rs.getString("nom_option"));
            opt.setPrixMensuel(rs.getFloat("prix_mensuel"));
            opt.setDescription(rs.getString("description"));
            return opt;
        }, idAgent);
    }
}
