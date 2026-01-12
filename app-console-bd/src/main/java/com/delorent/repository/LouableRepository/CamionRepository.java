package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.Camion;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.model.Louable.SqlClause;
import com.delorent.repository.RepositoryBase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Repository
public class CamionRepository implements RepositoryBase<Camion, Integer> {

    private final JdbcTemplate jdbcTemplate;
    private final ThreadLocal<Boolean> lastDisponibleLeJour = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public CamionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDernierDisponibleLeJour(Camion ignored) {
        return Boolean.TRUE.equals(lastDisponibleLeJour.get());
    }

    public VehiculeSummary toSummary(Camion camion, boolean dispoJour) {
        return new VehiculeSummary(
                new LouableSummary(camion.getIdLouable(), camion.getStatut(), camion.getPrixJour(), camion.getLieuPrincipal(), "Camion", dispoJour),
                camion.getMarque(),
                camion.getModele(),
                camion.getAnnee(),
                camion.getCouleur(),
                camion.getImmatriculation(),
                camion.getKilometrage(),
                "Camion"
        );
    }

    @Override
    public List<Camion> getAll() { throw new UnsupportedOperationException(); }

    @Override
    public Camion get(Integer id) { throw new UnsupportedOperationException(); }

    @Override
    public Integer add(Camion entity) { throw new UnsupportedOperationException(); }

    @Override
    public boolean modify(Camion entity) { throw new UnsupportedOperationException(); }

    @Override
    public boolean delete(Integer id) { throw new UnsupportedOperationException(); }

    public List<Camion> getCatalogue(LocalDate dateCible, boolean uniquementDisponibles, List<LouableFiltre> filtres) {

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
                "SELECT l.*, v.*, ca.*," +
                " CASE WHEN " + dispoExpr + " THEN 1 ELSE 0 END AS disponibleLeJour" +
                " FROM LOUABLE l" +
                " JOIN VEHICULE v ON l.id = v.id" +
                " JOIN CAMION ca ON v.id = ca.id" +
                " WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();
        params.add(d); params.add(d);

        if (uniquementDisponibles) {
            sql.append(" AND ").append(dispoExpr);
            params.add(d); params.add(d);
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

            return new Camion(
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
                    rs.getInt("chargeMaxKg"),
                    rs.getDouble("volumeUtileM3"),
                    rs.getDouble("hauteurM"),
                    rs.getDouble("longueurM"),
                    rs.getString("permisRequis")
            );
        });
    }
}