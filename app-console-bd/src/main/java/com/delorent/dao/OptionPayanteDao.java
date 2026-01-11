package com.delorent.dao;

import com.delorent.model.OptionPayante;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OptionPayanteDao {

    private final JdbcTemplate jdbcTemplate;

    public OptionPayanteDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OptionPayante> findByAgent(int idAgent) {
        String sql = """
            SELECT idOption, nomOption, description, prixMensuel, status
            FROM OPTION_PAYANTE
            WHERE idProprietaire = ?
        """;

        return jdbcTemplate.query(sql, new Object[]{idAgent}, (rs, rowNum) -> {
            OptionPayante o = new OptionPayante();
            o.setIdOption(rs.getInt("idOption"));
            o.setNomOption(rs.getString("nomOption"));
            o.setDescription(rs.getString("description"));
            o.setPrixMensuel(rs.getDouble("prixMensuel"));
            o.setStatus(rs.getString("status"));
            return o;
        });
    }

    public void activer(int idOption) {
        jdbcTemplate.update(
            "UPDATE OPTION_PAYANTE SET status = 'ACTIVE' WHERE idOption = ?",
            idOption
        );
    }

    public void desactiver(int idOption) {
        jdbcTemplate.update(
            "UPDATE OPTION_PAYANTE SET status = 'INACTIVE' WHERE idOption = ?",
            idOption
        );
    }
}
