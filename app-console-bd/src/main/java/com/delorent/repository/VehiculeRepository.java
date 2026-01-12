package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VehiculeRepository {

    private final JdbcTemplate jdbc;

    public VehiculeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * CREATE Vehicule (partie enfant)
     */
    public int createVehicule(int id,
                              String modele,
                              int annee,
                              String couleur,
                              String immatriculation,
                              int kilometrage) {

        String sql = """
            INSERT INTO VEHICULE (id, modele, annee, couleur, immatriculation, kilometrage)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        return jdbc.update(sql, id, modele, annee, couleur, immatriculation, kilometrage);
    }

    /**
     * UPDATE Vehicule
     */
    public int updateVehicule(int id,
                              String modele,
                              int annee,
                              String couleur,
                              String immatriculation,
                              int kilometrage) {

        String sql = """
            UPDATE VEHICULE
            SET modele = ?, annee = ?, couleur = ?, immatriculation = ?, kilometrage = ?
            WHERE id = ?
        """;

        return jdbc.update(sql, modele, annee, couleur, immatriculation, kilometrage, id);
    }

    /**
     * DELETE Vehicule
     */
    public int deleteVehicule(int id) {
        String sql = "DELETE FROM VEHICULE WHERE id = ?";
        return jdbc.update(sql, id);
    }

    /**
     * EXISTS Vehicule
     */
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM VEHICULE WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}

