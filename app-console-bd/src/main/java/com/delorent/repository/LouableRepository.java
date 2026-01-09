package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class LouableRepository {

    private final JdbcTemplate jdbc;

    public LouableRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllLouablesForForm() {
        return jdbc.queryForList(
                """
                SELECT l.id AS idLouable,
                       l.marque AS marque,
                       v.modele AS modele,
                       v.immatriculation AS immatriculation,
                       l.prixJour AS prixJour,
                       l.statut AS statut,
                       l.lieuPrincipal AS lieuPrincipal
                FROM LOUABLE l
                LEFT JOIN VEHICULE v ON v.id = l.id
                ORDER BY l.id ASC
                """
        );
    }
}