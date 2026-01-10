package com.delorent.repository;

import com.delorent.model.Agent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Repository
public class AgentRepository implements RepositoryBase<Agent, Long> {

    private final JdbcTemplate jdbcTemplate;

    public AgentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Tables / Colonnes
    private static final String T_USER = "UTILISATEUR";
    private static final String T_AGENT = "AGENT";

    private static final String COL_ID = "idUtilisateur";
    private static final String COL_MAIL = "mail";
    private static final String COL_MDP = "mdp";
    private static final String COL_ADR = "adresse";
    private static final String COL_VILLE = "ville";
    private static final String COL_CP = "codePostal";
    private static final String COL_REGION = "region";
    private static final String COL_TEL = "telephone";

    private static final String COL_NOM = "nom";
    private static final String COL_PRENOM = "prenom";

    private Agent mapRow(ResultSet rs) throws java.sql.SQLException {
        return new Agent(
                rs.getInt(COL_ID),
                rs.getString(COL_MAIL),
                rs.getString(COL_MDP),
                rs.getString(COL_ADR),
                rs.getString(COL_VILLE),
                rs.getString(COL_CP),
                rs.getString(COL_REGION),
                rs.getString(COL_TEL),
                rs.getString(COL_NOM),
                rs.getString(COL_PRENOM)
        );
    }

    @Override
    public List<Agent> getAll() {
        String sql =
                "SELECT u." + COL_ID + ", u." + COL_MAIL + ", u." + COL_MDP + ", " +
                        "u." + COL_ADR + ", u." + COL_VILLE + ", u." + COL_CP + ", u." + COL_REGION + ", u." + COL_TEL + ", " +
                        "a." + COL_NOM + ", a." + COL_PRENOM + " " +
                "FROM " + T_USER + " u " +
                "JOIN " + T_AGENT + " a ON a." + COL_ID + " = u." + COL_ID;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    @Override
    public Agent get(Long id) {
        String sql =
                "SELECT u." + COL_ID + ", u." + COL_MAIL + ", u." + COL_MDP + ", " +
                        "u." + COL_ADR + ", u." + COL_VILLE + ", u." + COL_CP + ", u." + COL_REGION + ", u." + COL_TEL + ", " +
                        "a." + COL_NOM + ", a." + COL_PRENOM + " " +
                "FROM " + T_USER + " u " +
                "JOIN " + T_AGENT + " a ON a." + COL_ID + " = u." + COL_ID + " " +
                "WHERE u." + COL_ID + " = ?";

        List<Agent> res = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Long add(Agent entity) {
        String sqlUtilisateur =
                "INSERT INTO " + T_USER +
                " (" + COL_MAIL + ", " + COL_MDP + ", " + COL_ADR + ", " + COL_VILLE + ", " + COL_CP + ", " + COL_REGION + ", " + COL_TEL + ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int insertedU = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlUtilisateur, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getMail());
            ps.setString(2, entity.getMotDePasse());
            ps.setString(3, entity.getAdresse());
            ps.setString(4, entity.getVille());
            ps.setString(5, entity.getCodePostal());
            ps.setString(6, entity.getRegion());
            ps.setString(7, entity.getTelephone());
            return ps;
        }, keyHolder);

        if (insertedU != 1) {
            throw new IllegalStateException("Insertion utilisateur échouée (inserted=" + insertedU + ")");
        }

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Impossible de récupérer l'id utilisateur généré");
        }
        long idUtilisateur = key.longValue();

        String sqlAgent =
                "INSERT INTO " + T_AGENT +
                " (" + COL_ID + ", " + COL_NOM + ", " + COL_PRENOM + ") " +
                "VALUES (?, ?, ?)";

        int insertedA = jdbcTemplate.update(sqlAgent, idUtilisateur, entity.getNom(), entity.getPrenom());
        if (insertedA != 1) {
            throw new IllegalStateException("Insertion agent échouée (inserted=" + insertedA + ")");
        }

        return idUtilisateur;
    }

    @Override
    public boolean modify(Agent entity) {
        String sqlUtilisateur =
                "UPDATE " + T_USER + " SET " +
                        COL_MAIL + " = ?, " + COL_MDP + " = ?, " + COL_ADR + " = ?, " + COL_VILLE + " = ?, " +
                        COL_CP + " = ?, " + COL_REGION + " = ?, " + COL_TEL + " = ? " +
                "WHERE " + COL_ID + " = ?";

        int u = jdbcTemplate.update(
                sqlUtilisateur,
                entity.getMail(),
                entity.getMotDePasse(),
                entity.getAdresse(),
                entity.getVille(),
                entity.getCodePostal(),
                entity.getRegion(),
                entity.getTelephone(),
                entity.getIdUtilisateur()
        );

        String sqlAgent =
                "UPDATE " + T_AGENT + " SET " +
                        COL_NOM + " = ?, " + COL_PRENOM + " = ? " +
                "WHERE " + COL_ID + " = ?";

        int a = jdbcTemplate.update(sqlAgent, entity.getNom(), entity.getPrenom(), entity.getIdUtilisateur());

        return u == 1 && a == 1;
    }

    @Override
    public boolean delete(Long id) {
        jdbcTemplate.update("DELETE FROM " + T_AGENT + " WHERE " + COL_ID + " = ?", id);
        int deleted = jdbcTemplate.update("DELETE FROM " + T_USER + " WHERE " + COL_ID + " = ?", id);
        return deleted == 1;
    }

    public Agent findByEmailAndPassword(String email, String password) {
        String sql =
                "SELECT u." + COL_ID + ", u." + COL_MAIL + ", u." + COL_MDP + ", " +
                        "u." + COL_ADR + ", u." + COL_VILLE + ", u." + COL_CP + ", u." + COL_REGION + ", u." + COL_TEL + ", " +
                        "a." + COL_NOM + ", a." + COL_PRENOM + " " +
                "FROM " + T_USER + " u " +
                "JOIN " + T_AGENT + " a ON a." + COL_ID + " = u." + COL_ID + " " +
                "WHERE u." + COL_MAIL + " = ? AND u." + COL_MDP + " = ?";

        var res = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), email, password);
        return res.isEmpty() ? null : res.get(0);
    }
}
