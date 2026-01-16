package com.delorent.repository;

import com.delorent.model.Assurance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class AssuranceRepository implements RepositoryBase<Assurance, Integer> {

    private final JdbcTemplate jdbcTemplate;

    public AssuranceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String T_ASSURANCE = "ASSURANCE";
    private static final String COL_ID = "idAssurance";
    private static final String COL_NOM = "nom";
    private static final String COL_PRIX_JOUR = "tarifJournalier";
    private static final String COL_FICHIER = "cheminFichier";
    private static final String COL_PROPRIO = "idProprietaire";

    private Assurance mapRow(ResultSet rs) throws java.sql.SQLException {
        return new Assurance(
                rs.getInt(COL_ID),
                rs.getString(COL_NOM),
                rs.getDouble(COL_PRIX_JOUR),
                rs.getString(COL_FICHIER),
                (Integer) rs.getObject(COL_PROPRIO)
        );
    }

    @Override
    public List<Assurance> getAll() {
        String sql = "SELECT * FROM " + T_ASSURANCE;
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs));
    }

    public List<Assurance> getByProprietaire(int idAgent) {
        String sql = "SELECT * FROM " + T_ASSURANCE + " WHERE " + COL_PROPRIO + " = ?";
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), idAgent);
    }

    public List<Assurance> getGlobalesEtAgent(int idAgent) {
        String sql = "SELECT * FROM " + T_ASSURANCE + " WHERE " + COL_PROPRIO + " IS NULL OR " + COL_PROPRIO + " = ?";
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), idAgent);
    }

    @Override
    public Assurance get(Integer id) {
        String sql = "SELECT * FROM " + T_ASSURANCE + " WHERE " + COL_ID + " = ?";
        var res = jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Integer add(Assurance entity) {
        String sql = "INSERT INTO " + T_ASSURANCE + " (" + COL_NOM + ", " + COL_PRIX_JOUR + ", " + COL_FICHIER + ", " + COL_PROPRIO + ") VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql, entity.getNom(), entity.getTarifJournalier(), entity.getCheminFichier(), entity.getIdProprietaire());
    }

    @Override
    public boolean modify(Assurance entity) {
        String sql = "UPDATE " + T_ASSURANCE + " SET " + COL_NOM + " = ?, " + COL_PRIX_JOUR + " = ?, " + COL_FICHIER + " = ? WHERE " + COL_ID + " = ?";
        int updated = jdbcTemplate.update(sql, entity.getNom(), entity.getTarifJournalier(), entity.getCheminFichier(), entity.getIdAssurance());
        return updated == 1;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM " + T_ASSURANCE + " WHERE " + COL_ID + " = ?";
        int deleted = jdbcTemplate.update(sql, id);
        return deleted == 1;
    }
}