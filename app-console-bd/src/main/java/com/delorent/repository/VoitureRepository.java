package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VoitureRepository {

    private final JdbcTemplate jdbc;

    public VoitureRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // CREATE (partie spécifique Voiture)
    public int createVoiture(int id, int nhPortes, int nbPlaces, int volumeCoffreLitres, String boite, String carburant, boolean climatisation) {
        String sql = """
            INSERT INTO VOITURE (id, nhPortes, nbPlaces, volumeCoffreLitres, boite, carburant, climatisation)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        return jdbc.update(sql, id, nhPortes, nbPlaces, volumeCoffreLitres, boite, carburant, climatisation);
    }

    // UPDATE
    public int updateVoiture(int id, int nhPortes, int nbPlaces, int volumeCoffreLitres, String boite, String carburant, boolean climatisation) {
        String sql = """
            UPDATE VOITURE
            SET nhPortes = ?, nbPlaces = ?, volumeCoffreLitres = ?, boite = ?, carburant = ?, climatisation = ?
            WHERE id = ?
        """;
        return jdbc.update(sql, nhPortes, nbPlaces, volumeCoffreLitres, boite, carburant, climatisation, id);
    }

    // DELETE
    public int deleteVoiture(int id) {
        String sql = "DELETE FROM VOITURE WHERE id = ?";
        return jdbc.update(sql, id);
    }

    // EXISTS
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM VOITURE WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
