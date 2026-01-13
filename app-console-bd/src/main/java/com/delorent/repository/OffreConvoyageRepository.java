package com.delorent.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.OffreConvoyage;

@Repository
public class OffreConvoyageRepository implements RepositoryBase<OffreConvoyage, Integer> {

    private final JdbcTemplate jdbcTemplate;

    public OffreConvoyageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private OffreConvoyage mapRow(ResultSet rs) throws SQLException {
        OffreConvoyage off = new OffreConvoyage(
            rs.getInt("idOffre"),
            rs.getInt("idLouable"),
            rs.getInt("idParkingArrivee"),
            rs.getDouble("reduction")
        );
        // On remplit les infos jointes si disponibles (via les alias)
        try {
            off.setNomParking(rs.getString("nomParking"));
            off.setVilleParking(rs.getString("villeParking"));
        } catch (SQLException e) { 
            // Colonnes non demandées dans la requête, on ignore
        }
        return off;
    }

    // Récupérer l'offre active pour un véhicule (avec les détails du parking)
    public OffreConvoyage getByLouable(int idLouable) {
        String sql = "SELECT o.*, p.nom as nomParking, p.ville as villeParking " +
                     "FROM OFFRE_CONVOYAGE o " +
                     "JOIN PARKING p ON o.idParkingArrivee = p.idParking " +
                     "WHERE o.idLouable = ?";
        List<OffreConvoyage> res = jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), idLouable);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Integer add(OffreConvoyage o) {
        String sql = "INSERT INTO OFFRE_CONVOYAGE (idLouable, idParkingArrivee, reduction) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, o.getIdLouable(), o.getIdParkingArrivee(), o.getReduction());
    }
    
    // Supprime l'offre (quand le trajet est terminé ou annulé par l'agent)
    public void deleteByLouable(int idLouable) {
        jdbcTemplate.update("DELETE FROM OFFRE_CONVOYAGE WHERE idLouable = ?", idLouable);
    }

    @Override public List<OffreConvoyage> getAll() { return List.of(); }
    @Override public OffreConvoyage get(Integer id) { return null; }
    @Override public boolean modify(OffreConvoyage entity) { return false; }
    @Override public boolean delete(Integer id) { return false; }
}