package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class DisponibiliteRepository {

    private final JdbcTemplate jdbc;

    public DisponibiliteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * IMPORTANT:
     * - On renvoie dateDebut/dateFin en STRING "YYYY-MM-DD" pour le JS (pas de "00:00:00")
     */
    public List<Map<String, Object>> findByLouable(int idLouable) {
        String sql = """
            SELECT
              idDisponibilite,
              idLouable,
              DATE_FORMAT(dateDebut, '%Y-%m-%d') AS dateDebut,
              DATE_FORMAT(dateFin,   '%Y-%m-%d') AS dateFin
            FROM DISPONIBILITE
            WHERE idLouable = ?
            ORDER BY dateDebut
        """;
        return jdbc.queryForList(sql, idLouable);
    }

    /**
     * Trouve un créneau qui couvre TOUTE la période demandée (inclusif).
     * Retourne la ligne (Map) ou null si rien.
     */
    public Map<String, Object> findOneCoveringRange(int idLouable, LocalDate dateDebut, LocalDate dateFin) {
        String sql = """
            SELECT
              idDisponibilite,
              idLouable,
              DATE(dateDebut) AS dateDebut,
              DATE(dateFin)   AS dateFin
            FROM DISPONIBILITE
            WHERE idLouable = ?
              AND DATE(dateDebut) <= ?
              AND DATE(dateFin)   >= ?
            ORDER BY dateDebut
            LIMIT 1
        """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, idLouable, Date.valueOf(dateDebut), Date.valueOf(dateFin));
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void deleteById(int idDisponibilite) {
        jdbc.update("DELETE FROM DISPONIBILITE WHERE idDisponibilite = ?", idDisponibilite);
    }

    /**
     * Insert un nouveau créneau. Retourne l'id auto-incrément.
     */
    public int insert(int idLouable, LocalDate dateDebut, LocalDate dateFin) {
        String sql = "INSERT INTO DISPONIBILITE (idLouable, dateDebut, dateFin) VALUES (?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idLouable);
            ps.setDate(2, Date.valueOf(dateDebut));
            ps.setDate(3, Date.valueOf(dateFin));
            return ps;
        }, kh);

        Number key = kh.getKey();
        return key == null ? -1 : key.intValue();
    }
}