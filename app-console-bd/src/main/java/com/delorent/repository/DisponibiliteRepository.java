package com.delorent.repository;

import com.delorent.model.Disponibilite;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DisponibiliteRepository implements RepositoryBase<Disponibilite, Integer> {

    private final JdbcTemplate jdbcTemplate;

    public DisponibiliteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String T_DISPO = "DISPONIBILITE";

    private static final String COL_ID = "idDisponibilite";
    private static final String COL_LOUABLE = "idLouable";
    private static final String COL_DEBUT = "dateDebut";
    private static final String COL_FIN = "dateFin";

    private Disponibilite mapRow(ResultSet rs) throws java.sql.SQLException {
        return new Disponibilite(
                rs.getInt(COL_ID),
                rs.getInt(COL_LOUABLE),
                rs.getDate(COL_DEBUT).toLocalDate(),
                rs.getDate(COL_FIN).toLocalDate()
        );
    }

    @Override
    public List<Disponibilite> getAll() {
        String sql = "SELECT " + COL_ID + ", " + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN +
                     " FROM " + T_DISPO;
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs));
    }

    public List<Disponibilite> getByLouable(int idLouable) {
        String sql = "SELECT " + COL_ID + ", " + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN +
                     " FROM " + T_DISPO +
                     " WHERE " + COL_LOUABLE + " = ? ORDER BY " + COL_DEBUT;
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), idLouable);
    }

    // Si tu en as besoin plus tard pour la location (logique “trouve un créneau couvrant”)
    public Disponibilite findOneCoveringRange(int idLouable, LocalDate debut, LocalDate fin) {
        String sql = "SELECT " + COL_ID + ", " + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN +
                     " FROM " + T_DISPO +
                     " WHERE " + COL_LOUABLE + " = ? AND " + COL_DEBUT + " <= ? AND " + COL_FIN + " >= ?" +
                     " ORDER BY " + COL_DEBUT + " LIMIT 1";
        var res = jdbcTemplate.query(sql, (rs, i) -> mapRow(rs),
                idLouable, Date.valueOf(debut), Date.valueOf(fin));
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Disponibilite get(Integer id) {
        String sql = "SELECT " + COL_ID + ", " + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN +
                     " FROM " + T_DISPO +
                     " WHERE " + COL_ID + " = ?";
        var res = jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Integer add(Disponibilite entity) {
        String sql = "INSERT INTO " + T_DISPO +
                     " (" + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN + ") VALUES (?, ?, ?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, entity.getIdLouable());
            ps.setDate(2, Date.valueOf(entity.getDateDebut()));
            ps.setDate(3, Date.valueOf(entity.getDateFin()));
            return ps;
        }, kh);

        Number key = kh.getKey();
        return key == null ? null : key.intValue();
    }

    @Override
    public boolean modify(Disponibilite entity) {
        String sql = "UPDATE " + T_DISPO + " SET " +
                     COL_LOUABLE + " = ?, " + COL_DEBUT + " = ?, " + COL_FIN + " = ? " +
                     "WHERE " + COL_ID + " = ?";

        int updated = jdbcTemplate.update(
                sql,
                entity.getIdLouable(),
                Date.valueOf(entity.getDateDebut()),
                Date.valueOf(entity.getDateFin()),
                entity.getIdDisponibilite()
        );
        return updated == 1;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM " + T_DISPO + " WHERE " + COL_ID + " = ?";
        int deleted = jdbcTemplate.update(sql, id);
        return deleted == 1;
    }
}
