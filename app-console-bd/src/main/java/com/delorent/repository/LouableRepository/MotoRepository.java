package com.delorent.repository.LouableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.delorent.model.Louable.Moto;
import com.delorent.model.Louable.SqlClause;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.TypeMoto;
import com.delorent.model.Louable.LouableFiltre;

import com.delorent.repository.RepositoryBase;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Repository
public class MotoRepository implements RepositoryBase<Moto, Integer> {

    private final JdbcTemplate jdbcTemplate;
    private final ThreadLocal<Boolean> lastDisponibleLeJour = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public MotoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDernierDisponibleLeJour(Moto ignored) {
        return Boolean.TRUE.equals(lastDisponibleLeJour.get());
    }

    public VehiculeSummary toSummary(Moto moto, boolean dispoJour) {
        return new VehiculeSummary(
                new LouableSummary(moto.getIdLouable(), moto.getStatut(), moto.getPrixJour(), moto.getLieuPrincipal(), "Moto", dispoJour),
                moto.getMarque(),
                moto.getModele(),
                moto.getAnnee(),
                moto.getCouleur(),
                moto.getImmatriculation(),
                moto.getKilometrage(),
                "Moto"
        );
    }

    @Override public List<Moto> getAll() { throw new UnsupportedOperationException(); }
    @Override public Moto get(Integer id) { throw new UnsupportedOperationException(); }
    @Override public Integer add(Moto entity) { throw new UnsupportedOperationException(); }
    @Override public boolean modify(Moto entity) { throw new UnsupportedOperationException(); }
    @Override public boolean delete(Integer id) { throw new UnsupportedOperationException(); }

    public List<Moto> getCatalogue(LocalDate dateCible, boolean uniquementDisponibles, List<LouableFiltre> filtres) {

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
                "SELECT l.*, v.*, m.*," +
                " CASE WHEN " + dispoExpr + " THEN 1 ELSE 0 END AS disponibleLeJour" +
                " FROM LOUABLE l" +
                " JOIN VEHICULE v ON l.id = v.id" +
                " JOIN MOTO m ON v.id = m.id" +
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

            return new Moto(
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
                    rs.getInt("cylindreeCc"),
                    rs.getInt("puissanceCh"),
                    TypeMoto.valueOf(rs.getString("typeMoto").toUpperCase()),
                    rs.getString("permisRequis")
            );
        });
    }
}