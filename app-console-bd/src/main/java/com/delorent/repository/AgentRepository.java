package com.delorent.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.AgentAmateur;
import com.delorent.model.Utilisateur.AgentProfessionnel;

@Repository
public class AgentRepository implements RepositoryBase<Agent, Long> {

    private final JdbcTemplate jdbcTemplate;

    public AgentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
    private static final String COL_TYPE = "typeAgent";
    private static final String COL_SIRET = "siret";

    private Agent mapRow(ResultSet rs) throws java.sql.SQLException {
        String type = rs.getString(COL_TYPE);
        
        int id = rs.getInt(COL_ID);
        String mail = rs.getString(COL_MAIL);
        String mdp = rs.getString(COL_MDP);
        String adr = rs.getString(COL_ADR);
        String ville = rs.getString(COL_VILLE);
        String cp = rs.getString(COL_CP);
        String region = rs.getString(COL_REGION);
        String tel = rs.getString(COL_TEL);
        String nom = rs.getString(COL_NOM);
        String prenom = rs.getString(COL_PRENOM);

        if ("PRO".equals(type)) {
            String siret = rs.getString(COL_SIRET);
            return new AgentProfessionnel(id, mail, mdp, adr, ville, cp, region, tel, nom, prenom, siret);
        } else {
            return new AgentAmateur(id, mail, mdp, adr, ville, cp, region, tel, nom, prenom);
        }
    }

    @Override
    public List<Agent> getAll() {
        String sql = "SELECT u.*, a.* FROM " + T_USER + " u JOIN " + T_AGENT + " a ON a." + COL_ID + " = u." + COL_ID;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    @Override
    public Agent get(Long id) {
        String sql = "SELECT u.*, a.* FROM " + T_USER + " u JOIN " + T_AGENT + " a ON a." + COL_ID + " = u." + COL_ID + " WHERE u." + COL_ID + " = ?";
        List<Agent> res = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Long add(Agent entity) {
        String sqlUtilisateur = "INSERT INTO " + T_USER + 
                " (" + COL_MAIL + ", " + COL_MDP + ", " + COL_ADR + ", " + COL_VILLE + ", " + COL_CP + ", " + COL_REGION + ", " + COL_TEL + ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
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

        long idUtilisateur = keyHolder.getKey().longValue();

        String sqlAgent = "INSERT INTO " + T_AGENT + 
                " (" + COL_ID + ", " + COL_NOM + ", " + COL_PRENOM + ", " + COL_TYPE + ", " + COL_SIRET + ") " +
                "VALUES (?, ?, ?, ?, ?)";
        
        String type = "AMATEUR";
        String siret = null;

        if (entity instanceof AgentProfessionnel pro) {
            type = "PRO";
            siret = pro.getSiret();
        }

        jdbcTemplate.update(sqlAgent, idUtilisateur, entity.getNom(), entity.getPrenom(), type, siret);

        return idUtilisateur;
    }

    @Override
    public boolean modify(Agent entity) {
        String sqlUtilisateur = "UPDATE " + T_USER + " SET " + COL_MAIL + "=?, " + COL_MDP + "=?, " + COL_ADR + "=?, " + COL_VILLE + "=?, " + COL_CP + "=?, " + COL_REGION + "=?, " + COL_TEL + "=? WHERE " + COL_ID + "=?";
        int u = jdbcTemplate.update(sqlUtilisateur, entity.getMail(), entity.getMotDePasse(), entity.getAdresse(), entity.getVille(), entity.getCodePostal(), entity.getRegion(), entity.getTelephone(), entity.getIdUtilisateur());

        String sqlAgent = "UPDATE " + T_AGENT + " SET " + COL_NOM + "=?, " + COL_PRENOM + "=? WHERE " + COL_ID + "=?";
        int a = jdbcTemplate.update(sqlAgent, entity.getNom(), entity.getPrenom(), entity.getIdUtilisateur());
        
        return u == 1 && a == 1;
    }
    
    @Override
    public boolean delete(Long id) {
        jdbcTemplate.update("DELETE FROM " + T_AGENT + " WHERE " + COL_ID + " = ?", id);
        return jdbcTemplate.update("DELETE FROM " + T_USER + " WHERE " + COL_ID + " = ?", id) == 1;
    }

    public Agent findByEmailAndPassword(String email, String password) {
        String sql = "SELECT u.*, a.* FROM " + T_USER + " u JOIN " + T_AGENT + " a ON a." + COL_ID + " = u." + COL_ID + " WHERE u." + COL_MAIL + " = ? AND u." + COL_MDP + " = ?";
        List<Agent> res = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), email, password);
        return res.isEmpty() ? null : res.get(0);
    }
}