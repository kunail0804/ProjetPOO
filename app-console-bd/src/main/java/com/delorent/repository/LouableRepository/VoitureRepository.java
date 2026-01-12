package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.Voiture;
import com.delorent.model.Louable.Carburant;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.TypeBoite;
import com.delorent.repository.RepositoryBase;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.model.Louable.SqlClause;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Repository
public class VoitureRepository implements RepositoryBase<Voiture,Integer> {

    private final JdbcTemplate jdbcTemplate;

    // hack simple: on mémorise le bool calculé lors du dernier mapping (évite de modifier tes entités)
    private final ThreadLocal<Boolean> lastDisponibleLeJour = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public VoitureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDernierDisponibleLeJour(Voiture ignored) {
        return Boolean.TRUE.equals(lastDisponibleLeJour.get());
    }

    public VehiculeSummary toSummary(Voiture voiture, boolean dispoJour) {
        return new VehiculeSummary(
                new LouableSummary(voiture.getIdLouable(), voiture.getStatut(), voiture.getPrixJour(), voiture.getLieuPrincipal(), "Voiture", dispoJour),
                voiture.getMarque(),
                voiture.getModele(),
                voiture.getAnnee(),
                voiture.getCouleur(),
                voiture.getImmatriculation(),
                voiture.getKilometrage(),
                "Voiture"
        );
    }

    @Override
    public List<Voiture> getAll() {
        String sql = "SELECT * FROM LOUABLE" +
                     " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                     " JOIN VOITURE ON VEHICULE.id = VOITURE.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Voiture(
                rs.getInt("id"),
                rs.getDouble("prixJour"),
                StatutLouable.valueOf(rs.getString("statut").toUpperCase()),
                rs.getString("lieuPrincipal"),
                rs.getString("marque"),
                rs.getString("modele"),
                rs.getInt("annee"),
                rs.getString("couleur"),
                rs.getString("immatriculation"),
                rs.getInt("kilometrage"),
                rs.getInt("nbPortes"),
                rs.getInt("nbPlaces"),
                rs.getInt("volumeCoffreLitres"),
                TypeBoite.valueOf(rs.getString("boite").toUpperCase()),
                Carburant.valueOf(rs.getString("carburant").toUpperCase()),
                rs.getBoolean("climatisation")
        ));
    }

    @Override
    public Voiture get(Integer id) {
        String sql = "SELECT * FROM LOUABLE" +
                     " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                     " JOIN VOITURE ON VEHICULE.id = VOITURE.id" +
                     " WHERE LOUABLE.id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new Voiture(
                rs.getInt("id"),
                rs.getDouble("prixJour"),
                StatutLouable.valueOf(rs.getString("statut").toUpperCase()),
                rs.getString("lieuPrincipal"),
                rs.getString("marque"),
                rs.getString("modele"),
                rs.getInt("annee"),
                rs.getString("couleur"),
                rs.getString("immatriculation"),
                rs.getInt("kilometrage"),
                rs.getInt("nbPortes"),
                rs.getInt("nbPlaces"),
                rs.getInt("volumeCoffreLitres"),
                TypeBoite.valueOf(rs.getString("boite").toUpperCase()),
                Carburant.valueOf(rs.getString("carburant").toUpperCase()),
                rs.getBoolean("climatisation")
        ));
    }

    @Override
    public Integer add(Voiture entity) {
        throw new UnsupportedOperationException("Use previous implementation if needed.");
    }

    @Override
    public boolean modify(Voiture entity) {
        throw new UnsupportedOperationException("Use previous implementation if needed.");
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Use previous implementation if needed.");
    }

    public List<Voiture> getCatalogue(LocalDate dateCible, boolean uniquementDisponibles, List<LouableFiltre> filtres) {

        Date d = Date.valueOf(dateCible);

        String dispoExpr =
                "EXISTS (" +
                "  SELECT 1 FROM DISPONIBILITE dp" +
                "  WHERE dp.idLouable = l.id" +
                "    AND dp.estReservee = 0" +
                "    AND DATE(dp.dateDebut) <= ?" +
                "    AND DATE(dp.dateFin)   >= ?" +
                ")";

        StringBuilder sql = new StringBuilder(
                "SELECT l.*, v.*, vo.*," +
                " CASE WHEN " + dispoExpr + " THEN 1 ELSE 0 END AS disponibleLeJour" +
                " FROM LOUABLE l" +
                " JOIN VEHICULE v ON l.id = v.id" +
                " JOIN VOITURE vo ON v.id = vo.id" +
                " WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        // params du CASE (2 fois)
        params.add(d);
        params.add(d);

        if (uniquementDisponibles) {
            sql.append(" AND ").append(dispoExpr);
            // params du filtre EXISTS (2 fois)
            params.add(d);
            params.add(d);
        }

        for (LouableFiltre filtre : filtres) {
            if (filtre.isActif()) {
                SqlClause clause = filtre.toSqlClause();
                if (clause.getPredicate() != null && !clause.getPredicate().isBlank()) {
                    sql.append(" AND ").append(clause.getPredicate());
                    params.addAll(clause.getParams());
                }
            }
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            lastDisponibleLeJour.set(rs.getInt("disponibleLeJour") == 1);

            return new Voiture(
                    rs.getInt("id"),
                    rs.getDouble("prixJour"),
                    StatutLouable.valueOf(rs.getString("statut").toUpperCase()),
                    rs.getString("lieuPrincipal"),
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getInt("annee"),
                    rs.getString("couleur"),
                    rs.getString("immatriculation"),
                    rs.getInt("kilometrage"),
                    rs.getInt("nbPortes"),
                    rs.getInt("nbPlaces"),
                    rs.getInt("volumeCoffreLitres"),
                    TypeBoite.valueOf(rs.getString("boite").toUpperCase()),
                    Carburant.valueOf(rs.getString("carburant").toUpperCase()),
                    rs.getBoolean("climatisation")
            );
        });
    }
}