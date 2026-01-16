package com.delorent.repository;

import com.delorent.model.Louable.Disponibilite;
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
    private static final String COL_RES = "estReservee";
    private static final String COL_PRIX = "prixJournalier";

    private Disponibilite mapRow(ResultSet rs) throws java.sql.SQLException {
        LocalDate debut = rs.getTimestamp(COL_DEBUT).toLocalDateTime().toLocalDate();
        LocalDate fin = rs.getTimestamp(COL_FIN).toLocalDateTime().toLocalDate();

        return new Disponibilite(
                rs.getInt(COL_ID),
                rs.getInt(COL_LOUABLE),
                debut,
                fin,
                rs.getBoolean(COL_RES),
                (Double) rs.getObject(COL_PRIX)
        );
    }

    @Override
    public List<Disponibilite> getAll() {
        String sql = "SELECT " + COL_ID + ", " + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN + ", " + COL_RES + ", " + COL_PRIX +
                " FROM " + T_DISPO;
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs));
    }

    public List<Disponibilite> getByLouable(int idLouable) {
        String sql = "SELECT " + COL_ID + ", " + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN + ", " + COL_RES + ", " + COL_PRIX +
                " FROM " + T_DISPO +
                " WHERE " + COL_LOUABLE + " = ? " +
                " ORDER BY " + COL_DEBUT;
        return jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), idLouable);
    }

    public Disponibilite findOneCoveringRange(int idLouable, LocalDate debut, LocalDate fin) {
        String sql = """
            SELECT idDisponibilite, idLouable, dateDebut, dateFin, estReservee, prixJournalier
            FROM DISPONIBILITE
            WHERE idLouable = ?
              AND estReservee = 0
              AND DATE(dateDebut) <= ?
              AND DATE(dateFin)   >= ?
            ORDER BY dateDebut
            LIMIT 1
        """;
        var res = jdbcTemplate.query(sql, (rs, i) -> mapRow(rs),
                idLouable, Date.valueOf(debut), Date.valueOf(fin));
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Disponibilite get(Integer id) {
        String sql = "SELECT " + COL_ID + ", " + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN + ", " + COL_RES + ", " + COL_PRIX +
                " FROM " + T_DISPO +
                " WHERE " + COL_ID + " = ?";
        var res = jdbcTemplate.query(sql, (rs, i) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Integer add(Disponibilite entity) {
        String sql = "INSERT INTO " + T_DISPO +
                " (" + COL_LOUABLE + ", " + COL_DEBUT + ", " + COL_FIN + ", " + COL_RES + ", " + COL_PRIX + ") VALUES (?, ?, ?, ?, ?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            var startTs = java.sql.Timestamp.valueOf(entity.getDateDebut().atStartOfDay());
            var endTs = java.sql.Timestamp.valueOf(entity.getDateFin().atTime(23, 59, 59));

            ps.setInt(1, entity.getIdLouable());
            ps.setTimestamp(2, startTs);
            ps.setTimestamp(3, endTs);
            ps.setBoolean(4, entity.isEstReservee());
            if (entity.getPrixJournalier() == null) ps.setNull(5, java.sql.Types.DOUBLE);
            else ps.setDouble(5, entity.getPrixJournalier());

            return ps;
        }, kh);

        Number key = kh.getKey();
        return key == null ? null : key.intValue();
    }

    @Override
    public boolean modify(Disponibilite entity) {
        String sql = "UPDATE " + T_DISPO + " SET " +
                COL_LOUABLE + " = ?, " + COL_DEBUT + " = ?, " + COL_FIN + " = ?, " + COL_RES + " = ?, " + COL_PRIX + " = ? " +
                "WHERE " + COL_ID + " = ?";

        var startTs = java.sql.Timestamp.valueOf(entity.getDateDebut().atStartOfDay());
        var endTs = java.sql.Timestamp.valueOf(entity.getDateFin().atTime(23, 59, 59));

        int updated = jdbcTemplate.update(
                sql,
                entity.getIdLouable(),
                startTs,
                endTs,
                entity.isEstReservee(),
                entity.getPrixJournalier(),
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

    public boolean existsOverlappingReserved(int idLouable, LocalDate debut, LocalDate fin) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM DISPONIBILITE
            WHERE idLouable = ?
              AND estReservee = 1
              AND NOT (DATE(dateFin) < ? OR DATE(dateDebut) > ?)
        """, Integer.class, idLouable, Date.valueOf(debut), Date.valueOf(fin));

        return count != null && count > 0;
    }

    public List<Disponibilite> findOverlappingOrAdjacentNonReserved(int idLouable, LocalDate debut, LocalDate fin) {
        LocalDate debutMinus1 = debut.minusDays(1);
        LocalDate finPlus1 = fin.plusDays(1);

        return jdbcTemplate.query("""
            SELECT idDisponibilite, idLouable, dateDebut, dateFin, estReservee, prixJournalier
            FROM DISPONIBILITE
            WHERE idLouable = ?
              AND estReservee = 0
              AND NOT (DATE(dateFin) < ? OR DATE(dateDebut) > ?)
            ORDER BY dateDebut
        """, (rs, i) -> mapRow(rs), idLouable, Date.valueOf(debutMinus1), Date.valueOf(finPlus1));
    }

    public Integer getProprietaireIdForLouable(int idLouable) {
        throw new UnsupportedOperationException("Unimplemented method 'getProprietaireIdForLouable'");
    }
}