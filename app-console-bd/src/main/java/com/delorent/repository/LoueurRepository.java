package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class LoueurRepository {

    private final JdbcTemplate jdbc;

    public LoueurRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllLoueurs() {
        return jdbc.queryForList(
                """
                SELECT l.idUtilisateur AS idLoueur,
                       l.prenom AS prenom,
                       l.nom AS nom
                FROM LOUEUR l
                ORDER BY l.idUtilisateur ASC
                """
        );
    }
}