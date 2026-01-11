package com.delorent.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NoteDao {

    private final JdbcTemplate jdbcTemplate;

    public NoteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Double getNoteGlobalePourVehicule(int idLouable) {

        String sql = """
            SELECT AVG(n.noteGlobale)
            FROM NOTES n
            JOIN CONTRAT c ON c.idContrat = n.idContrat
            WHERE c.idLouable = ?
        """;

        return jdbcTemplate.queryForObject(sql, Double.class, idLouable);
    }
}