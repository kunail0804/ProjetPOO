package com.delorent.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.Parking;

@Repository
public class ParkingRepository implements RepositoryBase<Parking, Integer> {

    private final JdbcTemplate jdbcTemplate;

    public ParkingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Parking mapRow(ResultSet rs) throws SQLException {
        return new Parking(
            rs.getInt("idParking"),
            rs.getString("nom"),
            rs.getString("ville"),
            rs.getString("adresse"),
            rs.getDouble("prixAgent")
        );
    }

    @Override
    public List<Parking> getAll() {
        return jdbcTemplate.query("SELECT * FROM PARKING", (rs, i) -> mapRow(rs));
    }

    @Override
    public Parking get(Integer id) {
        List<Parking> res = jdbcTemplate.query("SELECT * FROM PARKING WHERE idParking = ?", (rs, i) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    // Pas nécessaire pour l'instant (géré par admin SQL)
    @Override public Integer add(Parking p) { return null; }
    @Override public boolean modify(Parking p) { return false; }
    @Override public boolean delete(Integer id) { return false; }
}