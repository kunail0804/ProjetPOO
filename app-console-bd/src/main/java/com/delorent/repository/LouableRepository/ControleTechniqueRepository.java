package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.ControleTechnique;
import com.delorent.model.Louable.ResultatControle;
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
public class ControleTechniqueRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public ControleTechniqueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // RowMapper pour convertir les résultats SQL en objets ControleTechnique
    private static class ControleTechniqueRowMapper implements RowMapper<ControleTechnique> {
        @Override
        public ControleTechnique mapRow(ResultSet rs, int rowNum) throws SQLException {
            ControleTechnique controle = new ControleTechnique();
            controle.setId(rs.getLong("id"));
            controle.setVehiculeId(rs.getLong("vehicule_id"));
            
            // Convertir java.sql.Date en LocalDate
            java.sql.Date sqlDateControle = rs.getDate("date_controle");
            if (sqlDateControle != null) {
                controle.setDateControle(sqlDateControle.toLocalDate());
            }
            
            java.sql.Date sqlDateValidite = rs.getDate("date_validite");
            if (sqlDateValidite != null) {
                controle.setDateValidite(sqlDateValidite.toLocalDate());
            }
            
            // Gérer l'enum ResultatControle
            String resultatStr = rs.getString("resultat");
            if (resultatStr != null) {
                controle.setResultat(ResultatControle.valueOf(resultatStr));
            }
            
            controle.setCentre(rs.getString("centre"));
            controle.setPrix(rs.getDouble("prix"));
            controle.setCommentaires(rs.getString("commentaires"));
            
            return controle;
        }
    }
    
    // 1. Trouver tous les contrôles d'un véhicule
    public List<ControleTechnique> findByVehiculeId(Long vehiculeId) {
        String sql = "SELECT * FROM CONTROLE_TECHNIQUE WHERE vehicule_id = ? ORDER BY date_controle DESC";
        return jdbcTemplate.query(sql, new ControleTechniqueRowMapper(), vehiculeId);
    }
    
    // 2. Trouver le dernier contrôle d'un véhicule
    public ControleTechnique findFirstByVehiculeIdOrderByDateControleDesc(Long vehiculeId) {
        String sql = "SELECT * FROM CONTROLE_TECHNIQUE WHERE vehicule_id = ? ORDER BY date_controle DESC LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new ControleTechniqueRowMapper(), vehiculeId);
        } catch (EmptyResultDataAccessException e) {
            return null; // Aucun contrôle trouvé
        }
    }
    
    // 3. Trouver un contrôle par son ID
    public Optional<ControleTechnique> findById(Long id) {
        String sql = "SELECT * FROM CONTROLE_TECHNIQUE WHERE id = ?";
        try {
            ControleTechnique controle = jdbcTemplate.queryForObject(sql, new ControleTechniqueRowMapper(), id);
            return Optional.ofNullable(controle);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    // 4. Sauvegarder un nouveau contrôle
    public void save(ControleTechnique controle) {
        if (controle.getId() == null) {
            // INSERT
            String sql = "INSERT INTO CONTROLE_TECHNIQUE " +
                        "(vehicule_id, date_controle, date_validite, resultat, centre, prix, commentaires) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            jdbcTemplate.update(sql,
                controle.getVehiculeId(),
                controle.getDateControle(),
                controle.getDateValidite(),
                controle.getResultat().name(), // Convertir enum en String
                controle.getCentre(),
                controle.getPrix(),
                controle.getCommentaires());
        } else {
            // UPDATE
            String sql = "UPDATE CONTROLE_TECHNIQUE SET " +
                        "vehicule_id = ?, date_controle = ?, date_validite = ?, " +
                        "resultat = ?, centre = ?, prix = ?, commentaires = ? " +
                        "WHERE id = ?";
            
            jdbcTemplate.update(sql,
                controle.getVehiculeId(),
                controle.getDateControle(),
                controle.getDateValidite(),
                controle.getResultat().name(),
                controle.getCentre(),
                controle.getPrix(),
                controle.getCommentaires(),
                controle.getId());
        }
    }
    
    // 5. Supprimer un contrôle
    public void deleteById(Long id) {
        String sql = "DELETE FROM CONTROLE_TECHNIQUE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    
    // 6. Vérifier si un véhicule a un contrôle technique valide
    public boolean hasControleValide(Long vehiculeId) {
        String sql = "SELECT COUNT(*) FROM CONTROLE_TECHNIQUE " +
                    "WHERE vehicule_id = ? " +
                    "AND resultat = 'VALIDE' " +
                    "AND date_validite > CURDATE()";
        
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, vehiculeId);
        return count != null && count > 0;
    }
    
    // 7. Compter le nombre de contrôles pour un véhicule
    public int countByVehiculeId(Long vehiculeId) {
        String sql = "SELECT COUNT(*) FROM CONTROLE_TECHNIQUE WHERE vehicule_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, vehiculeId);
        return count != null ? count : 0;
    }

   

    
    
}