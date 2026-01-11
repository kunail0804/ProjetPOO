package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.LouableFiltre;
import com.delorent.model.SqlClause;
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

    public List<LouableSummary> getDisponibles(List<LouableFiltre> filtres) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT l.* FROM ").append(T_LOUABLE).append(" l ");
        sql.append("WHERE l.").append(COL_STATUT).append(" = ? ");
        params.add(StatutLouable.DISPONIBLE.name());

        for (LouableFiltre f : filtres) {
            if (f != null && f.isActif()) {
                SqlClause c = f.toSqlClause();
                sql.append("AND ").append(c.predicate()).append(" ");
                params.addAll(c.params());
            }
        }

        sql.append("ORDER BY l.").append(COL_PRIXJOUR).append(" ASC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, i) -> new LouableSummary(
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