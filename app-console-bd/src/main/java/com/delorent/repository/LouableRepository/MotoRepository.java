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
    // Ajout de US.L.10 pour la gestion de la disponibilité dans le catalogue
    private final ThreadLocal<Boolean> lastDisponibleLeJour = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public MotoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Helper de US.L.10
    public boolean isDernierDisponibleLeJour(Moto ignored) {
        return Boolean.TRUE.equals(lastDisponibleLeJour.get());
    }

    // --- Opérations CRUD (Gardées de HEAD pour la gestion propriétaire) ---

    @Override
    public List<Moto> getAll() {
        String sql = "SELECT * FROM LOUABLE" +
                " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                " JOIN MOTO ON VEHICULE.id = MOTO.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Moto(
                rs.getInt("id"),
                rs.getInt("idProprietaire"), // On garde l'idProprietaire
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
        ));
    }

    @Override
    public Moto get(Integer id) {
        String sql = "SELECT * FROM LOUABLE" +
                " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                " JOIN MOTO ON VEHICULE.id = MOTO.id" +
                " WHERE LOUABLE.id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new Moto(
                rs.getInt("id"),
                rs.getInt("idProprietaire"),
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
        ));
    }

    @Override
    public Integer add(Moto entity) {
        String sqlLouable = "INSERT INTO LOUABLE (idProprietaire, prixJour, statut, lieuPrincipal) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlLouable, entity.getIdAgent(), entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal());

        Integer idLouable = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        String sqlVehicule = "INSERT INTO VEHICULE (id, marque, modele, annee, couleur, immatriculation, kilometrage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlVehicule, idLouable, entity.getMarque(), entity.getModele(), entity.getAnnee(),
                entity.getCouleur(), entity.getImmatriculation(), entity.getKilometrage());

        String sqlMoto = "INSERT INTO MOTO (id, cylindreeCc, puissanceCh, typeMoto, permisRequis) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlMoto, idLouable, entity.getCylindreeCc(), entity.getPuissanceCh(), entity.getTypeMoto().name(), entity.getPermisRequis());

        return idLouable;
    }

    @Override
    public boolean modify(Moto entity) {
        String sqlLouable = "UPDATE LOUABLE SET prixJour = ?, statut = ?, lieuPrincipal = ? WHERE id = ?";
        jdbcTemplate.update(sqlLouable, entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal(), entity.getIdLouable());

        String sqlVehicule = "UPDATE VEHICULE SET marque = ?, modele = ?, annee = ?, couleur = ?, immatriculation = ?, kilometrage = ? WHERE id = ?";
        jdbcTemplate.update(sqlVehicule, entity.getMarque(), entity.getModele(), entity.getAnnee(), entity.getCouleur(),
                entity.getImmatriculation(), entity.getKilometrage(), entity.getIdLouable());

        String sqlMoto = "UPDATE MOTO SET cylindreeCc = ?, puissanceCh = ?, typeMoto = ?, permisRequis = ? WHERE id = ?";
        jdbcTemplate.update(sqlMoto, entity.getCylindreeCc(), entity.getPuissanceCh(), entity.getTypeMoto().name(),
                entity.getPermisRequis(), entity.getIdLouable());

        return true;
    }

    @Override
    public boolean delete(Integer id) {
        String sqlMoto = "DELETE FROM MOTO WHERE id = ?";
        jdbcTemplate.update(sqlMoto, id);

        String sqlVehicule = "DELETE FROM VEHICULE WHERE id = ?";
        jdbcTemplate.update(sqlVehicule, id);

        String sqlLouable = "DELETE FROM LOUABLE WHERE id = ?";
        jdbcTemplate.update(sqlLouable, id);

        return true;
    }

    // --- Méthodes de Recherche Avancée (Fusion US.L.10 et HEAD) ---

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
            // Logique US.L.10 pour traquer la dispo
            if (rs.getObject("disponibleLeJour") != null) {
                lastDisponibleLeJour.set(rs.getInt("disponibleLeJour") == 1);
            }

            return new Moto(
                    rs.getInt("id"),
                    rs.getInt("idProprietaire"), // Inclus
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

    // Gardé pour compatibilité HEAD
    public List<Moto> getDisponibles(List<LouableFiltre> filtres) {
        return getCatalogue(LocalDate.now(), true, filtres);
    }

    // Gardé pour compatibilité HEAD (Gestion Propriétaire)
    public List<Moto> getByProprietaire(int idProprietaire) {
        String sql = "SELECT * FROM LOUABLE l " +
                "JOIN VEHICULE v ON l.id = v.id " +
                "JOIN MOTO m ON v.id = m.id " +
                "WHERE l.idProprietaire = ?";
        return jdbcTemplate.query(sql, new Object[]{idProprietaire}, (rs, rowNum) -> new Moto(
                rs.getInt("id"),
                rs.getInt("idProprietaire"),
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
        ));
    }
    public VehiculeSummary toSummary(Moto moto, boolean dispoJour) {
        return new VehiculeSummary(
                new LouableSummary(
                    moto.getIdLouable(),
                    moto.getIdAgent(), // <--- AJOUTER ICI
                    moto.getStatut(),
                    moto.getPrixJour(),
                    moto.getLieuPrincipal(),
                    "Moto",
                    dispoJour
                ),
                moto.getMarque(),
                moto.getModele(),
                moto.getAnnee(),
                moto.getCouleur(),
                moto.getImmatriculation(),
                moto.getKilometrage(),
                "Moto"
        );
    }
}