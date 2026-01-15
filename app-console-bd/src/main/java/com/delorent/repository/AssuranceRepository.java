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
    // Nouveaux champs BDD
    private static final String COL_FICHIER = "cheminFichier";
    private static final String COL_PROPRIO = "idProprietaire";

    private Assurance mapRow(ResultSet rs) throws java.sql.SQLException {
        return new Assurance(
                rs.getInt(COL_ID),
                rs.getString(COL_NOM),
                rs.getDouble(COL_PRIX_JOUR),
                rs.getString(COL_FICHIER),   // Nouveau
                (Integer) rs.getObject(COL_PROPRIO) // Nouveau (peut être null)
        );
    }

    @Override
    public List<Assurance> getAll() {
        // On récupère tout (Globales + celles des agents)
        String sql = "SELECT * FROM " + T_ASSURANCE;
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs));
    }

    /**
     * Récupère uniquement les assurances créées par un Agent spécifique
     */
    public List<Assurance> getByProprietaire(int idAgent) {
        String sql = "SELECT * FROM " + T_ASSURANCE + " WHERE " + COL_PROPRIO + " = ?";
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), idAgent);
    }

    /**
     * Récupère les assurances globales (Système) + celles de cet agent spécifique
     * (Utile pour la liste déroulante lors de la location plus tard)
     */
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
        // Insertion avec les nouveaux champs
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