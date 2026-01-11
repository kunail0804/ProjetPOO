package com.delorent.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.delorent.model.EntrepriseEntretien;
import com.delorent.model.Tarif;

@Repository
public class EntrepriseEntretienRepository implements RepositoryBase<EntrepriseEntretien, Long> {

    private final JdbcTemplate jdbcTemplate;

    public EntrepriseEntretienRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Tables / Colonnes
    private static final String T_USER = "UTILISATEUR";
    private static final String T_ENT = "ENTREPRISE_ENTRETIEN";

    private static final String COL_ID = "idUtilisateur";
    private static final String COL_MAIL = "mail";
    private static final String COL_MDP = "mdp";
    private static final String COL_ADR = "adresse";
    private static final String COL_VILLE = "ville";
    private static final String COL_CP = "codePostal";
    private static final String COL_REGION = "region";
    private static final String COL_TEL = "telephone";

    private static final String COL_NOM_ENT = "nomEntreprise";
    private static final String COL_RS = "raisonSoc";
    private static final String COL_SIRET = "noSiret";

    private EntrepriseEntretien mapRow(ResultSet rs) throws java.sql.SQLException {
        return new EntrepriseEntretien(
                rs.getInt(COL_ID),
                rs.getString(COL_MAIL),
                rs.getString(COL_MDP),
                rs.getString(COL_ADR),
                rs.getString(COL_VILLE),
                rs.getString(COL_CP),
                rs.getString(COL_REGION),
                rs.getString(COL_TEL),
                rs.getString(COL_NOM_ENT),
                rs.getString(COL_RS),
                rs.getString(COL_SIRET)
        );
    }

    @Override
    public List<EntrepriseEntretien> getAll() {
        String sql =
                "SELECT u." + COL_ID + ", u." + COL_MAIL + ", u." + COL_MDP + ", " +
                        "u." + COL_ADR + ", u." + COL_VILLE + ", u." + COL_CP + ", u." + COL_REGION + ", u." + COL_TEL + ", " +
                        "e." + COL_NOM_ENT + ", e." + COL_RS + ", e." + COL_SIRET + " " +
                "FROM " + T_USER + " u " +
                "JOIN " + T_ENT + " e ON e." + COL_ID + " = u." + COL_ID;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    @Override
    public EntrepriseEntretien get(Long id) {
        String sql =
                "SELECT u." + COL_ID + ", u." + COL_MAIL + ", u." + COL_MDP + ", " +
                        "u." + COL_ADR + ", u." + COL_VILLE + ", u." + COL_CP + ", u." + COL_REGION + ", u." + COL_TEL + ", " +
                        "e." + COL_NOM_ENT + ", e." + COL_RS + ", e." + COL_SIRET + " " +
                "FROM " + T_USER + " u " +
                "JOIN " + T_ENT + " e ON e." + COL_ID + " = u." + COL_ID + " " +
                "WHERE u." + COL_ID + " = ?";

        List<EntrepriseEntretien> res = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Long add(EntrepriseEntretien entity) {
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

        String sqlEntreprise =
                "INSERT INTO " + T_ENT +
                " (" + COL_ID + ", " + COL_NOM_ENT + ", " + COL_RS + ", " + COL_SIRET + ") " +
                "VALUES (?, ?, ?, ?)";

        int insertedE = jdbcTemplate.update(
                sqlEntreprise,
                idUtilisateur,
                entity.getNomEntreprise(),
                entity.getRaisonSoc(),
                entity.getNoSiret()
        );

        if (insertedE != 1) {
            throw new IllegalStateException("Insertion entreprise d'entretien échouée (inserted=" + insertedE + ")");
        }

        return idUtilisateur;
    }

    @Override
    public boolean modify(EntrepriseEntretien entity) {
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

        String sqlEntreprise =
                "UPDATE " + T_ENT + " SET " +
                        COL_NOM_ENT + " = ?, " + COL_RS + " = ?, " + COL_SIRET + " = ? " +
                "WHERE " + COL_ID + " = ?";

        int e = jdbcTemplate.update(
                sqlEntreprise,
                entity.getNomEntreprise(),
                entity.getRaisonSoc(),
                entity.getNoSiret(),
                entity.getIdUtilisateur()
        );

        return u == 1 && e == 1;
    }

    @Override
    public boolean delete(Long id) {
        jdbcTemplate.update("DELETE FROM " + T_ENT + " WHERE " + COL_ID + " = ?", id);
        int deleted = jdbcTemplate.update("DELETE FROM " + T_USER + " WHERE " + COL_ID + " = ?", id);
        return deleted == 1;
    }

    public EntrepriseEntretien findByEmailAndPassword(String email, String password) {
        String sql =
                "SELECT u." + COL_ID + ", u." + COL_MAIL + ", u." + COL_MDP + ", " +
                        "u." + COL_ADR + ", u." + COL_VILLE + ", u." + COL_CP + ", u." + COL_REGION + ", u." + COL_TEL + ", " +
                        "a." + COL_NOM_ENT + ", a." + COL_RS + ", a." + COL_SIRET + " " +
                "FROM " + T_USER + " u " +
                "JOIN " + T_ENT + " a ON a." + COL_ID + " = u." + COL_ID + " " +
                "WHERE u." + COL_MAIL + " = ? AND u." + COL_MDP + " = ?";

        var res = jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), email, password);
        return res.isEmpty() ? null : res.get(0);
    }

    public EntrepriseEntretien trouverParId(int id) {
        return get((long) id);
    }

    public List<Tarif> trouverTarifs(int idEntreprise) {
        String sql = "SELECT * FROM TARIF_ENTRETIEN WHERE idEntreprise = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Tarif.class), idEntreprise);
    }

    public void ajouterTarif(Tarif tarif) {
        String sql = "INSERT INTO TARIF_ENTRETIEN (idEntreprise, typeVehicule, modele, prixForfait) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, tarif.getIdEntreprise(), tarif.getTypeVehicule(), tarif.getModele(), tarif.getPrixForfait());
    }
}
