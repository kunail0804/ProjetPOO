package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CamionRepository {

    private final JdbcTemplate jdbc;

    public CamionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ================= CREATE =================
    public int createCamion(int id,
                            int chargeMaxKg,
                        double volumeUtileM3,
                        double hauteurM,
                        double longueurM,
                        String permisRequis) { 

        String sql = """
            INSERT INTO CAMION (id, chargeMaxKg, volumeUtileM3, hauteurM, longueurM, permisRequis)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        return jdbc.update(sql, id, chargeMaxKg, volumeUtileM3, hauteurM, longueurM, permisRequis);
    }

    // ================= UPDATE =================
    public int updateCamion(int id,
                            Integer chargeMaxKg,
                            Double volumeUtileM3,
                            Double hauteurM,
                            Double longueurM,
                            String permisRequis) {

        String sql = """
            UPDATE CAMION
            SET chargeMaxKg = ?,
                volumeUtileM3 = ?,
                hauteurM = ?,
                longueurM = ?,
                permisRequis = ?
            WHERE id = ?
        """;

        return jdbc.update(sql, chargeMaxKg, volumeUtileM3, hauteurM, longueurM, permisRequis, id);
    }

    // ================= DELETE =================
    public int deleteCamion(int id) {
        String sql = "DELETE FROM CAMION WHERE id = ?";
        return jdbc.update(sql, id);
    }

    // ================= EXISTS =================
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM CAMION WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
