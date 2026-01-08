package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VehiculeRepository {

    private final JdbcTemplate jdbc;

    public VehiculeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Add VEHICULE */
    public int addVehicule(int id,
                           String marque,
                           String modele,
                           String immatriculation) {

        String sql = """
            INSERT INTO VEHICULE (id_louable, marque, modele, immatriculation)
            VALUES (?, ?, ?, ?)
        """;

        return jdbc.update(sql, id, marque, modele, immatriculation);
    }

    /** Update VEHICULE */
    public int updateVehicule(int id,
                              String marque,
                              String modele,
                              String immatriculation) {

        String sql = """
            UPDATE VEHICULE
            SET marque = ?, modele = ?, immatriculation = ?
            WHERE id_louable = ?
        """;

        return jdbc.update(sql, marque, modele, immatriculation, id);
    }

    /** Delete VEHICULE */
    public int deleteVehicule(int idLouable) {
        String sql = "DELETE FROM VEHICULE WHERE id_louable = ?";
        return jdbc.update(sql, idLouable);
    }
}
