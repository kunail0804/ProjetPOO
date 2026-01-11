package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.StatutLouable;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Repository
public class LouableRepository  implements RepositoryBase<LouableSummary, Integer> {

    private final JdbcTemplate jdbcTemplate;

    public LouableRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Tables / Colonnes
    private static final String T_LOUABLE = "LOUABLE";
    private static final String COL_ID = "id";
    private static final String COL_PRIXJOUR = "prixJour";
    private static final String COL_STATUT = "statut";
    private static final String COL_LIEU = "lieuPrincipal";

    @Override
    public List<LouableSummary> getAll() {
        String sql = "SELECT * FROM " + T_LOUABLE;
        return jdbcTemplate.query(sql, (rs, i) -> new LouableSummary(
            rs.getInt(COL_ID),
            rs.getDouble(COL_PRIXJOUR),
            StatutLouable.valueOf(rs.getString(COL_STATUT)),
            rs.getString(COL_LIEU)
        ));
    }

    public List<LouableSummary> getAllDisponible() {
        String sql = "SELECT * FROM " + T_LOUABLE + " WHERE " + COL_STATUT + " = ?";
        return jdbcTemplate.query(sql, new Object[]{StatutLouable.DISPONIBLE.name()}, (rs, i) -> new LouableSummary(
            rs.getInt(COL_ID),
            rs.getDouble(COL_PRIXJOUR),
            StatutLouable.valueOf(rs.getString(COL_STATUT)),
            rs.getString(COL_LIEU)
        ));
    }

    @Override
    public LouableSummary get(Integer id) {
        String sql = "SELECT * FROM " + T_LOUABLE + " WHERE " + COL_ID + " = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, i) -> new LouableSummary(
            rs.getInt(COL_ID),
            rs.getDouble(COL_PRIXJOUR),
            StatutLouable.valueOf(rs.getString(COL_STATUT)),
            rs.getString(COL_LIEU)
        ));
    }

    @Override
    public Integer add(LouableSummary entity) {
        String sql = "INSERT INTO " + T_LOUABLE + " (" +
            COL_PRIXJOUR + ", " +
            COL_STATUT + ", " +
            COL_LIEU +
            ") VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql,
            entity.prixJour(),
            entity.statut().name(),
            entity.lieuPrincipal()
        );
    }

    @Override
    public boolean modify(LouableSummary entity) {
        String sql = "UPDATE " + T_LOUABLE + " SET " +
            COL_PRIXJOUR + " = ?, " +
            COL_STATUT + " = ?, " +
            COL_LIEU + " = ? " +
            "WHERE " + COL_ID + " = ?";
        int rowsAffected = jdbcTemplate.update(sql,
            entity.prixJour(),
            entity.statut().name(),
            entity.lieuPrincipal(),
            entity.idLouable()
        );
        return rowsAffected > 0;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM " + T_LOUABLE + " WHERE " + COL_ID + " = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
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