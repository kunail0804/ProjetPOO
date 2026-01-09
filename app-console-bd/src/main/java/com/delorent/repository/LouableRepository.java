package com.delorent.repository;

import com.delorent.model.StatutLouable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class LouableRepository {

    private final JdbcTemplate jdbc;

    public LouableRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * CREATE Louable
     */
    public int createLouable(int id,    
                             int idProprietaire,
                             String marque,
                             double prixJour,
                             StatutLouable statut,
                             String lieuPrincipal) {

        String sql = """
            INSERT INTO LOUABLE (id, idProprietaire, marque, prixJour, statut, lieuPrincipal)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, id);
            ps.setInt(2, idProprietaire);
            ps.setString(3, marque);
            ps.setDouble(4, prixJour);
            ps.setString(5, statut.name());
            ps.setString(6, lieuPrincipal);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) throw new IllegalStateException("No generated key returned for Louable");
        return key.intValue();
    }

    /** UPDATE louable */
    public int updateLouable(int id,
                             int idProprietaire,
                             String marque,
                             double prixJour,
                             StatutLouable statut,
                             String lieuPrincipal) {

        String sql = """
            UPDATE LOUABLE
            SET idProprietaire = ?, marque = ?, prixJour = ?, statut = ?, lieuPrincipal = ?
            WHERE idLouable = ?
        """;

        return jdbc.update(sql, idProprietaire, marque, prixJour, statut.name(), lieuPrincipal, id);
    }

    /** DELETE Louable selon id */
    public int deleteLouable(int id) {
        String sql = "DELETE FROM LOUABLE WHERE id = ?";
        return jdbc.update(sql, id);
    }

    /** Check if Louable exists by id and proprietaire */
    public boolean existsByIdAndProprietaire(int id, int idProprietaire) {
        String sql = "SELECT COUNT(*) FROM LOUABLE WHERE id = ? AND idProprietaire = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, id, idProprietaire);
        return count != null && count > 0;
    }
}
