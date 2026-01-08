package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long countUsers() {
        // ⚠️ Mets ici le VRAI nom de table si c'est UTILISATEUR
        return jdbc.queryForObject("SELECT COUNT(*) FROM UTILISATEUR", Long.class);
    }

    public List<Map<String, Object>> findAllUsersRaw() {
        return jdbc.query("SELECT * FROM UTILISATEUR", (rs, rowNum) -> {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= colCount; i++) {
                row.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            return row;
        });
    }
}
