package com.delorent.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LouableRepository {

    private final JdbcTemplate jdbcTemplate;

    public LouableRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> rechercherVehicules(String ville, String marque, Integer anneeMin, Double prixMax) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // 1. SELECT : On récupère toutes les nouvelles infos
        // J'utilise des alias pour être sûr des noms en Java
        sql.append("SELECT l.idLouable AS id, ");
        sql.append("       l.prixJour AS prix, ");
        sql.append("       l.lieuPrincipal AS ville, "); // Nouvelle colonne
        sql.append("       v.marque AS marque, ");
        sql.append("       v.modele AS modele, ");
        sql.append("       v.annee AS annee, ");         // Nouvelle colonne
        sql.append("       v.kilometrage AS km, ");      // Nouvelle colonne
        sql.append("       v.immatriculation AS immatriculation ");
        
        sql.append("FROM VEHICULE v ");
        sql.append("JOIN LOUABLE l ON v.id_louable = l.idLouable ");
        sql.append("WHERE 1=1 ");

        // 2. CRITÈRES DE SÉLECTION DYNAMIQUES

        // Filtre Ville (lieuPrincipal)
        if (ville != null && !ville.trim().isEmpty()) {
            sql.append("AND l.lieuPrincipal LIKE ? ");
            params.add("%" + ville + "%");
        }

        // Filtre Marque
        if (marque != null && !marque.trim().isEmpty()) {
            sql.append("AND v.marque LIKE ? ");
            params.add("%" + marque + "%");
        }

        // Filtre Année Minimum (ex: cherche voiture plus récente que 2018)
        if (anneeMin != null) {
            sql.append("AND v.annee >= ? ");
            params.add(anneeMin);
        }

        // Filtre Prix Maximum (ex: budget max 50€)
        if (prixMax != null) {
            sql.append("AND l.prixJour <= ? ");
            params.add(prixMax);
        }

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }
}