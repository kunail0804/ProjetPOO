package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReleveKilometrageRepository {

    private final JdbcTemplate jdbc;

    public ReleveKilometrageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record ReleveView(
            int idReleve,
            int idContrat,
            String typeReleve,
            int kilometrage,
            String photoPath,
            LocalDateTime dateSaisie
    ) {}

    public ReleveView findByContratAndType(int idContrat, String typeReleve) {
        String sql = """
            SELECT idReleve, idContrat, typeReleve, kilometrage, photoPath, dateSaisie
            FROM RELEVE_KILOMETRAGE
            WHERE idContrat = ? AND typeReleve = ?
        """;
        List<ReleveView> res = jdbc.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("dateSaisie");
            return new ReleveView(
                    rs.getInt("idReleve"),
                    rs.getInt("idContrat"),
                    rs.getString("typeReleve"),
                    rs.getInt("kilometrage"),
                    rs.getString("photoPath"),
                    ts == null ? null : ts.toLocalDateTime()
            );
        }, idContrat, typeReleve);

        return res.isEmpty() ? null : res.get(0);
    }

    public int insert(int idContrat, String typeReleve, int kilometrage, String photoPath) {
        String sql = """
            INSERT INTO RELEVE_KILOMETRAGE (idContrat, typeReleve, kilometrage, photoPath)
            VALUES (?, ?, ?, ?)
        """;
        return jdbc.update(sql, idContrat, typeReleve, kilometrage, photoPath);
    }
}