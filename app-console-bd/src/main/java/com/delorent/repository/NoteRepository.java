package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class NoteRepository {

    private final JdbcTemplate jdbc;

    public NoteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findAllCriteres() {
        String sql = "SELECT id, libelle FROM CRITERE_LOUABLE ORDER BY id";
        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> critere = new LinkedHashMap<>();
            critere.put("id", rs.getInt("id"));
            critere.put("libelle", rs.getString("libelle"));
            return critere;
        });
    }

    public boolean contratDejaNote(int idContrat) {
        String sql = "SELECT COUNT(*) FROM NOTE_LOUABLE WHERE idContrat = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, idContrat);
        return count != null && count > 0;
    }

    public Map<String, Object> findContratById(int idContrat) {
        String sql =
                "SELECT c.idContrat, c.dateDebut, c.dateFin, " +
                "       l.lieuPrincipal AS lieu, " +
                "       v.marque, v.modele " +
                "FROM CONTRAT c " +
                "JOIN LOUABLE l ON c.idLouable = l.id " +
                "LEFT JOIN VEHICULE v ON v.id = l.id " +
                "WHERE c.idContrat = ?";

        List<Map<String, Object>> rows = jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> contrat = new LinkedHashMap<>();
            contrat.put("idContrat", rs.getInt("idContrat"));
            contrat.put("dateDebut", rs.getDate("dateDebut"));
            contrat.put("dateFin", rs.getDate("dateFin"));
            contrat.put("lieu", rs.getString("lieu"));
            contrat.put("marque", rs.getString("marque"));
            contrat.put("modele", rs.getString("modele"));
            return contrat;
        }, idContrat);

        return rows.isEmpty() ? null : rows.get(0);
    }

    public void sauvegarderNote(int idContrat,
                                double noteGlobale,
                                String commentaire,
                                Map<Integer, Integer> notesCriteres) {

        String sqlNote = "INSERT INTO NOTE_LOUABLE (noteGlobale, commentaire, idContrat) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sqlNote, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, noteGlobale);
            ps.setString(2, (commentaire == null) ? "" : commentaire);
            ps.setInt(3, idContrat);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Impossible de récupérer l'id de la note insérée.");
        }
        int idNote = key.intValue();

        String sqlCrit = "INSERT INTO NOTE_LOUABLE_CRITERE (idNote, idCritere, valeur) VALUES (?, ?, ?)";
        for (Map.Entry<Integer, Integer> e : notesCriteres.entrySet()) {
            jdbc.update(sqlCrit, idNote, e.getKey(), e.getValue());
        }
    }

    public List<Map<String, Object>> findNotesByVehicule(int idLouable) {
        String sql =
                "SELECT n.id, n.noteGlobale, n.commentaire, n.dateEvaluation, c.idContrat " +
                "FROM NOTE_LOUABLE n " +
                "JOIN CONTRAT c ON n.idContrat = c.idContrat " +
                "WHERE c.idLouable = ? " +
                "ORDER BY n.dateEvaluation DESC";

        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> note = new LinkedHashMap<>();
            note.put("id", rs.getInt("id"));
            note.put("noteGlobale", rs.getDouble("noteGlobale"));
            note.put("commentaire", rs.getString("commentaire"));
            note.put("dateEvaluation", rs.getDate("dateEvaluation"));
            note.put("idContrat", rs.getInt("idContrat"));
            return note;
        }, idLouable);
    }

    public Double findMoyenneByLouable(int idLouable) {
        String sql =
                "SELECT ROUND(AVG(n.noteGlobale), 1) AS moyenne " +
                "FROM NOTE_LOUABLE n " +
                "JOIN CONTRAT c ON n.idContrat = c.idContrat " +
                "WHERE c.idLouable = ?";

        return jdbc.query(sql, rs -> {
            if (!rs.next()) return null;
            double v = rs.getDouble("moyenne");
            return rs.wasNull() ? null : v;
        }, idLouable);
    }

    public Map<Integer, Double> findMoyennesByLouables(List<Integer> idsLouables) {
        Map<Integer, Double> out = new HashMap<>();
        if (idsLouables == null || idsLouables.isEmpty()) return out;

        StringJoiner sj = new StringJoiner(",", "(", ")");
        Object[] params = new Object[idsLouables.size()];
        for (int i = 0; i < idsLouables.size(); i++) {
            sj.add("?");
            params[i] = idsLouables.get(i);
        }

        String sql =
                "SELECT c.idLouable, ROUND(AVG(n.noteGlobale), 1) AS moyenne " +
                "FROM NOTE_LOUABLE n " +
                "JOIN CONTRAT c ON n.idContrat = c.idContrat " +
                "WHERE c.idLouable IN " + sj +
                "GROUP BY c.idLouable";

        jdbc.query(sql, rs -> {
            int idLouable = rs.getInt("idLouable");
            double v = rs.getDouble("moyenne");
            if (!rs.wasNull()) out.put(idLouable, v);
        }, params);

        return out;
    }

    public Double findMoyenneByLouableFromCriteres(int idLouable) {
        String sql = """
            SELECT ROUND(AVG(t.noteContrat), 1) AS moyenne
            FROM (
              SELECT c.idContrat, AVG(nlc.valeur) AS noteContrat
              FROM CONTRAT c
              JOIN NOTE_LOUABLE nl          ON nl.idContrat = c.idContrat
              JOIN NOTE_LOUABLE_CRITERE nlc ON nlc.idNote = nl.id
              WHERE c.idLouable = ?
                AND nlc.valeur IS NOT NULL
              GROUP BY c.idContrat
            ) t
        """;

        return jdbc.query(sql, rs -> {
            if (!rs.next()) return null;
            double v = rs.getDouble("moyenne");
            return rs.wasNull() ? null : v;
        }, idLouable);
    }

    public int countNotesByLouableFromCriteres(int idLouable) {
        String sql = """
            SELECT COUNT(*) AS nb
            FROM (
              SELECT c.idContrat
              FROM CONTRAT c
              JOIN NOTE_LOUABLE nl          ON nl.idContrat = c.idContrat
              JOIN NOTE_LOUABLE_CRITERE nlc ON nlc.idNote = nl.id
              WHERE c.idLouable = ?
                AND nlc.valeur IS NOT NULL
              GROUP BY c.idContrat
            ) t
        """;

        Integer nb = jdbc.queryForObject(sql, Integer.class, idLouable);
        return nb == null ? 0 : nb;
    }

    public Map<Integer, Double> findMoyennesByLouablesFromCriteres(List<Integer> idsLouables) {
        Map<Integer, Double> out = new HashMap<>();
        if (idsLouables == null || idsLouables.isEmpty()) return out;

        StringJoiner sj = new StringJoiner(",", "(", ")");
        Object[] params = new Object[idsLouables.size()];
        for (int i = 0; i < idsLouables.size(); i++) {
            sj.add("?");
            params[i] = idsLouables.get(i);
        }

        String sql =
                "SELECT t.idLouable, ROUND(AVG(t.noteContrat), 1) AS moyenne " +
                "FROM ( " +
                "  SELECT c.idLouable, c.idContrat, AVG(nlc.valeur) AS noteContrat " +
                "  FROM CONTRAT c " +
                "  JOIN NOTE_LOUABLE nl ON nl.idContrat = c.idContrat " +
                "  JOIN NOTE_LOUABLE_CRITERE nlc ON nlc.idNote = nl.id " +
                "  WHERE c.idLouable IN " + sj +
                "    AND nlc.valeur IS NOT NULL " +
                "  GROUP BY c.idLouable, c.idContrat " +
                ") t " +
                "GROUP BY t.idLouable";

        jdbc.query(sql, rs -> {
            int idLouable = rs.getInt("idLouable");
            double v = rs.getDouble("moyenne");
            if (!rs.wasNull()) out.put(idLouable, v);
        }, params);

        return out;
    }
}