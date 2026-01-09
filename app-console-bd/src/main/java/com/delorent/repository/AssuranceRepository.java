package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AssuranceRepository {

    private final JdbcTemplate jdbc;

    public AssuranceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllAssurances() {
        return jdbc.queryForList(
                """
                SELECT idAssurance, nom, tarifJournalier
                FROM ASSURANCE
                ORDER BY idAssurance ASC
                """
        );
    }
}