package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
public class DisponibiliteRepository {

    private final JdbcTemplate jdbc;

    public DisponibiliteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Renvoie les disponibilités d'un louable (créneaux) triées par date_debut.
     * Table attendue : DISPONIBILITE(id_louable, date_debut, date_fin, est_reservee, prix_journalier, ...)
     */
    public List<Map<String, Object>> findDisponibilitesByLouable(int idLouable) {
        return jdbc.queryForList(
                """
                SELECT date_debut, date_fin, est_reservee, prix_journalier
                FROM DISPONIBILITE
                WHERE id_louable = ?
                ORDER BY date_debut
                """,
                idLouable
        );
    }

    /**
     * Optionnel : si tu veux aussi afficher le lieu principal (lieu de prise) en haut.
     * Ici on le prend depuis LOUABLE.lieuPrincipal.
     */
    public String findLieuPrincipal(int idLouable) {
        return jdbc.queryForObject(
                "SELECT lieuPrincipal FROM LOUABLE WHERE id = ?",
                String.class,
                idLouable
        );
    }
}