package com.delorent.repository;

// 1. On importe les classes depuis le BON package (ReleveKM)
import com.delorent.model.ReleveKM.ReleveKilometrage;
import com.delorent.model.ReleveKM.ReleveType;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReleveKilometrageRepository {

    private final JdbcTemplate jdbc;
    private static final String TABLE = "RELEVE_KILOMETRAGE";

    public ReleveKilometrageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ReleveKilometrage> getByContrat(int idContrat) {
        String sql = """
                SELECT idReleve, idContrat, typeReleve, kilometrage, photoPath, dateSaisie
                FROM RELEVE_KILOMETRAGE
                WHERE idContrat = ?
                ORDER BY dateSaisie ASC
                """;
        return jdbc.query(sql, (rs, rowNum) -> map(rs), idContrat);
    }

    public int countByContrat(int idContrat) {
        String sql = "SELECT COUNT(*) FROM " + TABLE + " WHERE idContrat = ?";
        Integer n = jdbc.queryForObject(sql, Integer.class, idContrat);
        return (n == null) ? 0 : n;
    }

    public boolean existsType(int idContrat, String typeReleve) {
        String sql = "SELECT COUNT(*) FROM " + TABLE + " WHERE idContrat = ? AND typeReleve = ?";
        Integer n = jdbc.queryForObject(sql, Integer.class, idContrat, typeReleve);
        return n != null && n > 0;
    }

    public int add(ReleveKilometrage r) {
        String sql = """
                INSERT INTO RELEVE_KILOMETRAGE (idContrat, typeReleve, kilometrage, photoPath, dateSaisie)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder kh = new GeneratedKeyHolder();

        int updated = jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, r.getIdContrat());
            
            // 2. Correction : On transforme l'Enum en String pour la base de données
            // r.getTypeReleve() renvoie un Enum, on ajoute .name()
            ps.setString(2, r.getTypeReleve().name()); 
            
            ps.setInt(3, r.getKilometrage());
            ps.setString(4, r.getPhotoPath());
            ps.setTimestamp(5, Timestamp.valueOf(r.getDateSaisie()));
            return ps;
        }, kh);

        if (updated != 1) throw new IllegalStateException("Insertion relevé échouée.");

        Number key = kh.getKey();
        return key == null ? 0 : key.intValue();
    }

    private ReleveKilometrage map(ResultSet rs) throws SQLException {
        // 3. Correction : On transforme le String de la BDD en Enum pour Java
        String typeStr = rs.getString("typeReleve");
        ReleveType typeEnum = ReleveType.valueOf(typeStr); 
        
        Timestamp ts = rs.getTimestamp("dateSaisie");
        LocalDateTime date = (ts == null) ? null : ts.toLocalDateTime();

        // 4. On passe l'Enum 'typeEnum' au constructeur, et plus le String
        return new ReleveKilometrage(
            rs.getInt("idReleve"),
            rs.getInt("idContrat"),
            typeEnum, // Ici c'est l'Enum qui est attendu
            rs.getInt("kilometrage"),
            rs.getString("photoPath"),
            date
        );
    }
}