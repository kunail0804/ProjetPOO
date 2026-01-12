package com.delorent.repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.delorent.model.Contrat;

@Repository
public class ContratRepository implements RepositoryBase<Contrat, Integer> {

    private final JdbcTemplate jdbc;

    public ContratRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String T_CONTRAT = "CONTRAT";
    private static final String COL_ID = "idContrat";
    private static final String COL_DEBUT = "dateDebut";
    private static final String COL_FIN = "dateFin";
    private static final String COL_LIEU_PRISE = "lieuPrise";
    private static final String COL_LIEU_DEPOT = "lieuDepot";

    // FK
    private static final String COL_ID_LOUEUR = "idLoueur";
    private static final String COL_ID_LOUABLE = "idLouable";
    private static final String COL_ID_ASSURANCE = "idAssurance";
    
    // NOUVEAU : Colonne Aller Simple
    private static final String COL_ID_PARKING = "idParkingRetour";

    // --- MAPPING (Factorisé pour éviter le code dupliqué) ---
    private Contrat mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Contrat c = new Contrat(
                rs.getInt(COL_ID),
                rs.getDate(COL_DEBUT).toLocalDate(),
                rs.getDate(COL_FIN).toLocalDate(),
                rs.getString(COL_LIEU_PRISE),
                rs.getString(COL_LIEU_DEPOT)
        );
        // On pourrait mapper les autres champs ici si nécessaire
        return c;
    }

    @Override
    public List<Contrat> getAll() {
        String sql = "SELECT * FROM " + T_CONTRAT + " ORDER BY " + COL_ID + " DESC";
        return jdbc.query(sql, (rs, i) -> mapRow(rs));
    }

    @Override
    public Contrat get(Integer id) {
        String sql = "SELECT * FROM " + T_CONTRAT + " WHERE " + COL_ID + " = ?";
        var res = jdbc.query(sql, (rs, i) -> mapRow(rs), id);
        return res.isEmpty() ? null : res.get(0);
    }

    /**
     * NOUVELLE MÉTHODE (Utilisée par LocationService)
     * Gère l'insertion complète avec l'option Parking (Aller Simple)
     */
    @Override
    public Integer add(Contrat entity) {
        String sql = "INSERT INTO " + T_CONTRAT + 
                " (" + COL_DEBUT + "," + COL_FIN + "," + COL_LIEU_PRISE + "," + COL_LIEU_DEPOT + "," +
                COL_ID_LOUEUR + "," + COL_ID_LOUABLE + "," + COL_ID_ASSURANCE + "," + COL_ID_PARKING + ") " +
                "VALUES (?,?,?,?,?,?,?,?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(entity.getDateDebut()));
            ps.setDate(2, Date.valueOf(entity.getDateFin()));
            ps.setString(3, entity.getLieuPrise());
            ps.setString(4, entity.getLieuDepot());
            ps.setInt(5, entity.getIdLoueur());
            ps.setInt(6, entity.getIdLouable());
            ps.setInt(7, entity.getIdAssurance());
            
            // Gestion optionnelle du parking
            if (entity.getIdParkingRetour() != null) {
                ps.setInt(8, entity.getIdParkingRetour());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("Impossible de récupérer l'idContrat généré");
        return key.intValue();
    }

    @Override
    public boolean modify(Contrat entity) {
        String sql = "UPDATE " + T_CONTRAT + " SET " +
                COL_DEBUT + "=?, " + COL_FIN + "=?, " + COL_LIEU_PRISE + "=?, " + COL_LIEU_DEPOT + "=? " +
                "WHERE " + COL_ID + "=?";

        int u = jdbc.update(sql,
                Date.valueOf(entity.getDateDebut()),
                Date.valueOf(entity.getDateFin()),
                entity.getLieuPrise(),
                entity.getLieuDepot(),
                entity.getId()
        );
        return u == 1;
    }

    @Override
    public boolean delete(Integer id) {
        int d = jdbc.update("DELETE FROM " + T_CONTRAT + " WHERE " + COL_ID + " = ?", id);
        return d == 1;
    }

    public List<Contrat> getByLoueurId(int idLoueur) {
        String sql = "SELECT * FROM " + T_CONTRAT +
                " WHERE " + COL_ID_LOUEUR + " = ?" +
                " ORDER BY " + COL_DEBUT + " DESC";
        return jdbc.query(sql, (rs, i) -> mapRow(rs), idLoueur);
    }

    // --- MÉTHODES EXISTANTES (CONSERVÉES POUR VOS COLLÈGUES) ---

    /**
     * Méthode Legacy : conservée pour compatibilité.
     * Note: J'ai dû mettre à jour le SQL pour inclure la colonne idParkingRetour (mis à NULL).
     */
    public int createContrat(LocalDate debut, LocalDate fin, String lieuPrise, String lieuDepot,
                             int idLoueur, int idLouable, int idAssurance) {

        // On insère NULL pour le parking car cette méthode ne gère pas l'option
        String sql = "INSERT INTO " + T_CONTRAT +
                " (" + COL_DEBUT + "," + COL_FIN + "," + COL_LIEU_PRISE + "," + COL_LIEU_DEPOT + "," +
                COL_ID_LOUEUR + "," + COL_ID_LOUABLE + "," + COL_ID_ASSURANCE + "," + COL_ID_PARKING + ") " +
                "VALUES (?,?,?,?,?,?,?, NULL)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(debut));
            ps.setDate(2, Date.valueOf(fin));
            ps.setString(3, lieuPrise);
            ps.setString(4, lieuDepot);
            ps.setInt(5, idLoueur);
            ps.setInt(6, idLouable);
            ps.setInt(7, idAssurance);
            return ps;
        }, kh);

        Number key = kh.getKey();
        if (key == null) throw new IllegalStateException("Impossible de récupérer l'idContrat généré");
        return key.intValue();
    }

    /**
     * Méthode Legacy : conservée pour compatibilité.
     */
    public boolean contratChevauche(int idLouable, LocalDate debut, LocalDate fin) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + T_CONTRAT +
                        " WHERE " + COL_ID_LOUABLE + " = ?" +
                        " AND NOT (" + COL_FIN + " < ? OR " + COL_DEBUT + " > ?)",
                Integer.class,
                idLouable, Date.valueOf(debut), Date.valueOf(fin)
        );
        return count != null && count > 0;
    }
}