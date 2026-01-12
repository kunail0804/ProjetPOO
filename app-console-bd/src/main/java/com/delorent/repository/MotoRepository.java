package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MotoRepository {

    private final JdbcTemplate jdbc;

    public MotoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ================= CREATE =================
    public int createMoto(int id,
                          Integer cylindreeCc,
                          Integer puissanceCh,
                          String typeMoto,
                          String permisRequis) {

        String sql = """
            INSERT INTO MOTO (id, cylindreeCc, puissanceCh, typeMoto, permisRequis)
            VALUES (?, ?, ?, ?, ?)
        """;

        return jdbc.update(sql, id, cylindreeCc, puissanceCh, typeMoto, permisRequis);
    }

    // ================= UPDATE =================
    public int updateMoto(int id,
                          Integer cylindreeCc,
                          Integer puissanceCh,
                          String typeMoto,
                          String permisRequis) {

        String sql = """
            UPDATE MOTO
            SET cylindreeCc = ?,
                puissanceCh = ?,
                typeMoto = ?,
                permisRequis = ?
            WHERE id = ?
        """;

        return jdbc.update(sql, cylindreeCc, puissanceCh, typeMoto, permisRequis, id);
    }

    // ================= DELETE =================
    public int deleteMoto(int id) {
        String sql = "DELETE FROM MOTO WHERE id = ?";
        return jdbc.update(sql, id);
    }

    // ================= EXISTS =================
    public boolean existsById(int id) {
        String sql = "SELECT COUNT(*) FROM MOTO WHERE id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
