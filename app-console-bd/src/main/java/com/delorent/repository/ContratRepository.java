package com.delorent.repository;

import com.delorent.model.Contrat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

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
    //private static final String COL_PRIX = "prixEstime"; // si tu as la colonne, sinon retire
    //private static final String COL_ETAT = "etat";       // si tu as la colonne, sinon retire

    // FK
    private static final String COL_ID_LOUEUR = "idLoueur";
    private static final String COL_ID_LOUABLE = "idLouable";
    private static final String COL_ID_ASSURANCE = "idAssurance";

    @Override
    public List<Contrat> getAll() {
        String sql = "SELECT " + COL_ID + "," + COL_DEBUT + "," + COL_FIN + "," + COL_LIEU_PRISE + "," + COL_LIEU_DEPOT +
                " FROM " + T_CONTRAT +
                " ORDER BY " + COL_ID + " DESC";


        return jdbc.query(sql, (rs, i) -> new Contrat(
                rs.getInt(COL_ID),
                rs.getDate(COL_DEBUT).toLocalDate(),
                rs.getDate(COL_FIN).toLocalDate(),
                rs.getString(COL_LIEU_PRISE),
                rs.getString(COL_LIEU_DEPOT)
        ));
    }

    @Override
    public Contrat get(Integer id) {
        String sql = "SELECT " + COL_ID + "," + COL_DEBUT + "," + COL_FIN + "," + COL_LIEU_PRISE + "," + COL_LIEU_DEPOT +
                " FROM " + T_CONTRAT + " WHERE " + COL_ID + " = ?";


        var res = jdbc.query(sql, (rs, i) -> new Contrat(
                rs.getInt(COL_ID),
                rs.getDate(COL_DEBUT).toLocalDate(),
                rs.getDate(COL_FIN).toLocalDate(),
                rs.getString(COL_LIEU_PRISE),
                rs.getString(COL_LIEU_DEPOT)
        ), id);

        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Integer add(Contrat entity) {
        // ⚠️ ici on ne met pas les FK (loueur/louable/assurance) car ton modèle Contrat ne les contient pas.
        // Donc on ne l’utilise PAS pour créer un contrat dans le cas location.
        throw new UnsupportedOperationException("Utilise createContrat(...) pour créer un contrat avec FK.");
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

    public int createContrat(LocalDate debut, LocalDate fin, String lieuPrise, String lieuDepot,
                             int idLoueur, int idLouable, int idAssurance) {

        String sql = "INSERT INTO " + T_CONTRAT +
                " (" + COL_DEBUT + "," + COL_FIN + "," + COL_LIEU_PRISE + "," + COL_LIEU_DEPOT + "," +
                COL_ID_LOUEUR + "," + COL_ID_LOUABLE + "," + COL_ID_ASSURANCE + ") " +
                "VALUES (?,?,?,?,?,?,?)";

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

    public List<Contrat> getByLoueurId(int idLoueur) {
        String sql = "SELECT " + COL_ID + "," + COL_DEBUT + "," + COL_FIN + "," + COL_LIEU_PRISE + "," + COL_LIEU_DEPOT +
                " FROM " + T_CONTRAT +
                " WHERE " + COL_ID_LOUEUR + " = ?" +
                " ORDER BY " + COL_DEBUT + " DESC";

        return jdbc.query(sql, (rs, i) -> new Contrat(
                rs.getInt(COL_ID),
                rs.getDate(COL_DEBUT).toLocalDate(),
                rs.getDate(COL_FIN).toLocalDate(),
                rs.getString(COL_LIEU_PRISE),
                rs.getString(COL_LIEU_DEPOT)
        ), idLoueur);
    }

     public record ContratDetailView(
            int idContrat,
            LocalDate dateDebut,
            LocalDate dateFin,
            String lieuPrise,
            String lieuDepot,
            int prix,
            String etat,
            int idLoueur,
            int idLouable,
            String marque,
            String modele,
            Integer annee,
            String immatriculation
    ) {}

    public ContratDetailView getDetailByIdAndLoueur(int idContrat, int idLoueur) {
        String sql = """
            SELECT c.idContrat, c.dateDebut, c.dateFin, c.lieuPrise, c.lieuDepot, c.prix, c.etat,
                   c.idLoueur, c.idLouable,
                   v.marque, v.modele, v.annee, v.immatriculation
            FROM CONTRAT c
            JOIN LOUABLE l ON l.id = c.idLouable
            JOIN VEHICULE v ON v.id = l.id
            WHERE c.idContrat = ? AND c.idLoueur = ?
        """;

        var res = jdbc.query(sql, (rs, i) -> new ContratDetailView(
                rs.getInt("idContrat"),
                rs.getDate("dateDebut") != null ? rs.getDate("dateDebut").toLocalDate() : null,
                rs.getDate("dateFin") != null ? rs.getDate("dateFin").toLocalDate() : null,
                rs.getString("lieuPrise"),
                rs.getString("lieuDepot"),
                rs.getInt("prix"),
                rs.getString("etat"),
                rs.getInt("idLoueur"),
                rs.getInt("idLouable"),
                rs.getString("marque"),
                rs.getString("modele"),
                (Integer) rs.getObject("annee"),
                rs.getString("immatriculation")
        ), idContrat, idLoueur);

        return res.isEmpty() ? null : res.get(0);
    }
}
