package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.StatutLouable;

import java.util.List;

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
}