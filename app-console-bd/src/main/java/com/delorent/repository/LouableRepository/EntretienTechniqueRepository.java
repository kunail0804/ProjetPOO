package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.EntretienTechnique;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class EntretienTechniqueRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public EntretienTechniqueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private static class EntretienTechniqueRowMapper implements RowMapper<EntretienTechnique> {
        @Override
        public EntretienTechnique mapRow(ResultSet rs, int rowNum) throws SQLException {
            EntretienTechnique entretien = new EntretienTechnique();
            entretien.setId(rs.getLong("id"));
            entretien.setVehiculeId(rs.getLong("vehicule_id"));
            entretien.setLibelle(rs.getString("libelle"));
            
            java.sql.Date sqlDate = rs.getDate("date_entretien");
            if (sqlDate != null) {
                entretien.setDateEntretien(sqlDate.toLocalDate());
            }
            
            entretien.setCompteRendu(rs.getString("compte_rendu"));
            
            Integer km = rs.getInt("kilometrage_effectue");
            if (!rs.wasNull()) {
                entretien.setKilometrageEffectue(km);
            }
            
            Double cout = rs.getDouble("cout");
            if (!rs.wasNull()) {
                entretien.setCout(cout);
            }
            
            entretien.setPrestataire(rs.getString("prestataire"));
            entretien.setPiecesChangees(rs.getString("pieces_changees"));
            
            return entretien;
        }
    }
    
    public List<EntretienTechnique> findByVehiculeId(Long vehiculeId) {
        String sql = "SELECT * FROM ENTRETIEN_TECHNIQUE WHERE vehicule_id = ? ORDER BY date_entretien DESC";
        return jdbcTemplate.query(sql, new EntretienTechniqueRowMapper(), vehiculeId);
    }
    
    public Optional<EntretienTechnique> findById(Long id) {
        String sql = "SELECT * FROM ENTRETIEN_TECHNIQUE WHERE id = ?";
        try {
            EntretienTechnique entretien = jdbcTemplate.queryForObject(sql, new EntretienTechniqueRowMapper(), id);
            return Optional.ofNullable(entretien);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    public void save(EntretienTechnique entretien) {
        if (entretien.getId() == null) {
            String sql = "INSERT INTO ENTRETIEN_TECHNIQUE " +
                        "(vehicule_id, libelle, date_entretien, compte_rendu, " +
                        "kilometrage_effectue, cout, prestataire, pieces_changees) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(sql,
                entretien.getVehiculeId(),
                entretien.getLibelle(),
                entretien.getDateEntretien(),
                entretien.getCompteRendu(),
                entretien.getKilometrageEffectue(),
                entretien.getCout(),
                entretien.getPrestataire(),
                entretien.getPiecesChangees());
        } else {
            String sql = "UPDATE ENTRETIEN_TECHNIQUE SET " +
                        "vehicule_id = ?, libelle = ?, date_entretien = ?, " +
                        "compte_rendu = ?, kilometrage_effectue = ?, cout = ?, " +
                        "prestataire = ?, pieces_changees = ? WHERE id = ?";
            
            jdbcTemplate.update(sql,
                entretien.getVehiculeId(),
                entretien.getLibelle(),
                entretien.getDateEntretien(),
                entretien.getCompteRendu(),
                entretien.getKilometrageEffectue(),
                entretien.getCout(),
                entretien.getPrestataire(),
                entretien.getPiecesChangees(),
                entretien.getId());
        }
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM ENTRETIEN_TECHNIQUE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    public List<EntretienTechnique> findRecentByVehiculeId(Long vehiculeId, int limit) {
        String sql = "SELECT * FROM ENTRETIEN_TECHNIQUE WHERE vehicule_id = ? " +
                    "ORDER BY date_entretien DESC LIMIT ?";
        return jdbcTemplate.query(sql, new EntretienTechniqueRowMapper(), vehiculeId, limit);
    }
}