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

    private static final String COL_ID_LOUEUR = "idLoueur";
    private static final String COL_ID_LOUABLE = "idLouable";
    private static final String COL_ID_ASSURANCE = "idAssurance";

    private static final String COL_ID_PARKING = "idParkingRetour";

    private static final String COL_PRIX = "prix";
    private static final String COL_ETAT = "etat";

    private Contrat mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Contrat c = new Contrat(
                rs.getInt(COL_ID),
                rs.getDate(COL_DEBUT).toLocalDate(),
                rs.getDate(COL_FIN).toLocalDate(),
                rs.getString(COL_LIEU_PRISE),
                rs.getString(COL_LIEU_DEPOT)
        );
        try { c.setPrix(rs.getBigDecimal(COL_PRIX)); } catch (Exception ignore) {}
        try { c.setEtat(rs.getString(COL_ETAT)); } catch (Exception ignore) {}
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

    @Override
    public Integer add(Contrat entity) {
        String sql = "INSERT INTO " + T_CONTRAT +
                " (" + COL_DEBUT + "," + COL_FIN + "," + COL_LIEU_PRISE + "," + COL_LIEU_DEPOT + "," +
                COL_PRIX + "," + COL_ETAT + "," +
                COL_ID_LOUEUR + "," + COL_ID_LOUABLE + "," + COL_ID_ASSURANCE + "," + COL_ID_PARKING + ") " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        KeyHolder kh = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setDate(1, Date.valueOf(entity.getDateDebut()));
            ps.setDate(2, Date.valueOf(entity.getDateFin()));
            ps.setString(3, entity.getLieuPrise());
            ps.setString(4, entity.getLieuDepot());

            ps.setBigDecimal(5, entity.getPrix() == null ? java.math.BigDecimal.ZERO : entity.getPrix());

            String etat = entity.getEtat();
            if (etat == null || etat.isBlank()) etat = "accepte";
            ps.setString(6, etat);

            ps.setInt(7, entity.getIdLoueur());
            ps.setInt(8, entity.getIdLouable());
            ps.setInt(9, entity.getIdAssurance());

            if (entity.getIdParkingRetour() != null) ps.setInt(10, entity.getIdParkingRetour());
            else ps.setNull(10, Types.INTEGER);

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

    public List<Contrat> getByAgentId(int idAgent) {
        String sql = """
            SELECT c.*
            FROM CONTRAT c
            JOIN LOUABLE l ON l.id = c.idLouable
            WHERE l.idProprietaire = ?
            ORDER BY c.dateDebut DESC
        """;
        return jdbc.query(sql, (rs, i) -> mapRow(rs), idAgent);
    }

    public record ContratDetailView(
            int idContrat,
            LocalDate dateDebut,
            LocalDate dateFin,
            String lieuPrise,
            String lieuDepot,
            java.math.BigDecimal prix,
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
                rs.getBigDecimal("prix"),
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

    public ContratDetailView getDetailByIdAndAgent(int idContrat, int idAgent) {
        String sql = """
            SELECT c.idContrat, c.dateDebut, c.dateFin, c.lieuPrise, c.lieuDepot, c.prix, c.etat,
                   c.idLoueur, c.idLouable,
                   v.marque, v.modele, v.annee, v.immatriculation
            FROM CONTRAT c
            JOIN LOUABLE l ON l.id = c.idLouable
            JOIN VEHICULE v ON v.id = l.id
            WHERE c.idContrat = ? AND l.idProprietaire = ?
        """;

        var res = jdbc.query(sql, (rs, i) -> new ContratDetailView(
                rs.getInt("idContrat"),
                rs.getDate("dateDebut") != null ? rs.getDate("dateDebut").toLocalDate() : null,
                rs.getDate("dateFin") != null ? rs.getDate("dateFin").toLocalDate() : null,
                rs.getString("lieuPrise"),
                rs.getString("lieuDepot"),
                rs.getBigDecimal("prix"),
                rs.getString("etat"),
                rs.getInt("idLoueur"),
                rs.getInt("idLouable"),
                rs.getString("marque"),
                rs.getString("modele"),
                (Integer) rs.getObject("annee"),
                rs.getString("immatriculation")
        ), idContrat, idAgent);

        return res.isEmpty() ? null : res.get(0);
    }

    public record ContratPrixDetailView(
            int idContrat,
            LocalDate dateDebut,
            LocalDate dateFin,
            String lieuPrise,
            String lieuDepot,
            java.math.BigDecimal prixBd,
            String etat,
            int idLoueur,
            int idLouable,
            String marque,
            String modele,
            Integer annee,
            String immatriculation,

            int nbJours,
            java.math.BigDecimal prixJourLouable,
            java.math.BigDecimal prixJourAssurance,
            java.math.BigDecimal baseLoueur,
            java.math.BigDecimal baseAssurance,
            java.math.BigDecimal commissionVariable,
            java.math.BigDecimal commissionFixe,
            java.math.BigDecimal commissionTotale,
            java.math.BigDecimal totalClient,
            java.math.BigDecimal prixCalcule
    ) {}

    public ContratPrixDetailView getPrixDetailByIdAndLoueur(int idContrat, int idLoueur) {
        String sql = """
            SELECT
              c.idContrat, c.dateDebut, c.dateFin, c.lieuPrise, c.lieuDepot,
              c.prix AS prix_bd, c.etat, c.idLoueur, c.idLouable,
              v.marque, v.modele, v.annee, v.immatriculation,

              (DATEDIFF(c.dateFin, c.dateDebut) + 1) AS nb_jours,

              CAST(l.prixJour AS DECIMAL(12,2)) AS prix_jour_louable,
              CAST(a.tarifJournalier AS DECIMAL(12,2)) AS prix_jour_assurance,

              ROUND(CAST(l.prixJour AS DECIMAL(12,2)) * (DATEDIFF(c.dateFin, c.dateDebut) + 1), 2) AS base_loueur,
              ROUND(CAST(a.tarifJournalier AS DECIMAL(12,2)) * (DATEDIFF(c.dateFin, c.dateDebut) + 1), 2) AS base_assurance,

              ROUND(
                (
                  (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                  * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                ) * 0.10,
                2
              ) AS commission_variable,

              ROUND((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2, 2) AS commission_fixe,

              ROUND(
                (
                  (
                    (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                    * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                  ) * 0.10
                ) + ((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2),
                2
              ) AS commission_totale,

              ROUND(
                (
                  (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                  * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                )
                + (
                  (
                    (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                    * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                  ) * 0.10
                )
                + ((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2),
                2
              ) AS total_client,

              ROUND(
                (
                  (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                  * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                )
                + (
                  (
                    (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                    * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                  ) * 0.10
                )
                + ((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2),
                2
              ) AS prix_calcule

            FROM CONTRAT c
            JOIN LOUABLE l ON l.id = c.idLouable
            JOIN VEHICULE v ON v.id = l.id
            JOIN ASSURANCE a ON a.idAssurance = c.idAssurance
            WHERE c.idContrat = ? AND c.idLoueur = ?
        """;

        var res = jdbc.query(sql, (rs, i) -> new ContratPrixDetailView(
                rs.getInt("idContrat"),
                rs.getDate("dateDebut") != null ? rs.getDate("dateDebut").toLocalDate() : null,
                rs.getDate("dateFin") != null ? rs.getDate("dateFin").toLocalDate() : null,
                rs.getString("lieuPrise"),
                rs.getString("lieuDepot"),
                rs.getBigDecimal("prix_bd"),
                rs.getString("etat"),
                rs.getInt("idLoueur"),
                rs.getInt("idLouable"),
                rs.getString("marque"),
                rs.getString("modele"),
                (Integer) rs.getObject("annee"),
                rs.getString("immatriculation"),

                rs.getInt("nb_jours"),
                rs.getBigDecimal("prix_jour_louable"),
                rs.getBigDecimal("prix_jour_assurance"),
                rs.getBigDecimal("base_loueur"),
                rs.getBigDecimal("base_assurance"),
                rs.getBigDecimal("commission_variable"),
                rs.getBigDecimal("commission_fixe"),
                rs.getBigDecimal("commission_totale"),
                rs.getBigDecimal("total_client"),
                rs.getBigDecimal("prix_calcule")
        ), idContrat, idLoueur);

        return res.isEmpty() ? null : res.get(0);
    }

    public ContratPrixDetailView getPrixDetailByIdAndAgent(int idContrat, int idAgent) {
        String sql = """
            SELECT
              c.idContrat, c.dateDebut, c.dateFin, c.lieuPrise, c.lieuDepot,
              c.prix AS prix_bd, c.etat, c.idLoueur, c.idLouable,
              v.marque, v.modele, v.annee, v.immatriculation,

              (DATEDIFF(c.dateFin, c.dateDebut) + 1) AS nb_jours,

              CAST(l.prixJour AS DECIMAL(12,2)) AS prix_jour_louable,
              CAST(a.tarifJournalier AS DECIMAL(12,2)) AS prix_jour_assurance,

              ROUND(CAST(l.prixJour AS DECIMAL(12,2)) * (DATEDIFF(c.dateFin, c.dateDebut) + 1), 2) AS base_loueur,
              ROUND(CAST(a.tarifJournalier AS DECIMAL(12,2)) * (DATEDIFF(c.dateFin, c.dateDebut) + 1), 2) AS base_assurance,

              ROUND(
                (
                  (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                  * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                ) * 0.10,
                2
              ) AS commission_variable,

              ROUND((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2, 2) AS commission_fixe,

              ROUND(
                (
                  (
                    (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                    * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                  ) * 0.10
                ) + ((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2),
                2
              ) AS commission_totale,

              ROUND(
                (
                  (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                  * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                )
                + (
                  (
                    (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                    * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                  ) * 0.10
                )
                + ((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2),
                2
              ) AS total_client,

              ROUND(
                (
                  (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                  * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                )
                + (
                  (
                    (CAST(l.prixJour AS DECIMAL(12,2)) + CAST(a.tarifJournalier AS DECIMAL(12,2)))
                    * (DATEDIFF(c.dateFin, c.dateDebut) + 1)
                  ) * 0.10
                )
                + ((DATEDIFF(c.dateFin, c.dateDebut) + 1) * 2),
                2
              ) AS prix_calcule

            FROM CONTRAT c
            JOIN LOUABLE l ON l.id = c.idLouable
            JOIN VEHICULE v ON v.id = l.id
            JOIN ASSURANCE a ON a.idAssurance = c.idAssurance
            WHERE c.idContrat = ? AND l.idProprietaire = ?
        """;

        var res = jdbc.query(sql, (rs, i) -> new ContratPrixDetailView(
                rs.getInt("idContrat"),
                rs.getDate("dateDebut") != null ? rs.getDate("dateDebut").toLocalDate() : null,
                rs.getDate("dateFin") != null ? rs.getDate("dateFin").toLocalDate() : null,
                rs.getString("lieuPrise"),
                rs.getString("lieuDepot"),
                rs.getBigDecimal("prix_bd"),
                rs.getString("etat"),
                rs.getInt("idLoueur"),
                rs.getInt("idLouable"),
                rs.getString("marque"),
                rs.getString("modele"),
                (Integer) rs.getObject("annee"),
                rs.getString("immatriculation"),

                rs.getInt("nb_jours"),
                rs.getBigDecimal("prix_jour_louable"),
                rs.getBigDecimal("prix_jour_assurance"),
                rs.getBigDecimal("base_loueur"),
                rs.getBigDecimal("base_assurance"),
                rs.getBigDecimal("commission_variable"),
                rs.getBigDecimal("commission_fixe"),
                rs.getBigDecimal("commission_totale"),
                rs.getBigDecimal("total_client"),
                rs.getBigDecimal("prix_calcule")
        ), idContrat, idAgent);

        return res.isEmpty() ? null : res.get(0);
    }

    public boolean contratAppartientAuLoueur(int idContrat, int idLoueur) {
        String sql = "SELECT COUNT(*) FROM CONTRAT WHERE idContrat = ? AND idLoueur = ?";
        Integer n = jdbc.queryForObject(sql, Integer.class, idContrat, idLoueur);
        return n != null && n > 0;
    }
}