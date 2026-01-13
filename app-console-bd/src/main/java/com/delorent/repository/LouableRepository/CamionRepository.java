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
    // ThreadLocal from US.L.10 for availability check in catalog
    private final ThreadLocal<Boolean> lastDisponibleLeJour = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public CamionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Helper method from US.L.10
    public boolean isDernierDisponibleLeJour(Camion ignored) {
        return Boolean.TRUE.equals(lastDisponibleLeJour.get());
    }

    // --- CRUD Operations (Kept from HEAD to support existing features) ---

    @Override
    public List<Camion> getAll() {
        String sql = "SELECT * FROM LOUABLE" +
                " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                " JOIN CAMION ON VEHICULE.id = CAMION.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Camion(
                rs.getInt("id"),
                rs.getInt("idProprietaire"), // Preserving idProprietaire
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
        ));
    }

    @Override
    public Camion get(Integer id) {
        String sql = "SELECT * FROM LOUABLE" +
                " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                " JOIN CAMION ON VEHICULE.id = CAMION.id" +
                " WHERE LOUABLE.id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new Camion(
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
                rs.getInt("chargeMaxKg"),
                rs.getDouble("volumeUtileM3"),
                rs.getDouble("hauteurM"),
                rs.getDouble("longueurM"),
                rs.getString("permisRequis")
        ));
    }

    @Override
    public Integer add(Camion entity) {
        String sqlLouable = "INSERT INTO LOUABLE (idProprietaire, prixJour, statut, lieuPrincipal) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlLouable, entity.getIdAgent(), entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal());

        Integer idLouable = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        String sqlVehicule = "INSERT INTO VEHICULE (id, marque, modele, annee, couleur, immatriculation, kilometrage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlVehicule, idLouable, entity.getMarque(), entity.getModele(), entity.getAnnee(), entity.getCouleur(), entity.getImmatriculation(), entity.getKilometrage());

        String sqlCamion = "INSERT INTO CAMION (id, chargeMaxKg, volumeUtileM3, hauteurM, longueurM, permisRequis) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlCamion, idLouable, entity.getChargeMaxKg(), entity.getVolumeUtileM3(), entity.getHauteurM(),
                entity.getLongueurM(), entity.getPermisRequis());

        return idLouable;
    }

    @Override
    public boolean modify(Camion entity) {
        String sqlLouable = "UPDATE LOUABLE SET prixJour = ?, statut = ?, lieuPrincipal = ? WHERE id = ?";
        jdbcTemplate.update(sqlLouable, entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal(), entity.getIdLouable());

        String sqlVehicule = "UPDATE VEHICULE SET marque = ?, modele = ?, annee = ?, couleur = ?, immatriculation = ?, kilometrage = ? WHERE id = ?";
        jdbcTemplate.update(sqlVehicule, entity.getMarque(), entity.getModele(), entity.getAnnee(), entity.getCouleur(),
                entity.getImmatriculation(), entity.getKilometrage(), entity.getIdLouable());

        String sqlCamion = "UPDATE CAMION SET chargeMaxKg = ?, volumeUtileM3 = ?, hauteurM = ?, longueurM = ?, permisRequis = ? WHERE id = ?";
        jdbcTemplate.update(sqlCamion, entity.getChargeMaxKg(), entity.getVolumeUtileM3(), entity.getHauteurM(),
                entity.getLongueurM(), entity.getPermisRequis(), entity.getIdLouable());

        return true;
    }

    @Override
    public boolean delete(Integer id) {
        // Implementation might be needed or keep as unsupported if not used yet
        throw new UnsupportedOperationException();
    }

    // --- Advanced Search Methods (Merged from US.L.10 and HEAD) ---

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
        // Parameters for the 'disponibleLeJour' check
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
            // Logic from US.L.10 to track availability
            if (rs.getObject("disponibleLeJour") != null) {
                lastDisponibleLeJour.set(rs.getInt("disponibleLeJour") == 1);
            }

            return new Camion(
                    rs.getInt("id"),
                    rs.getInt("idProprietaire"), // Ensure idProprietaire is included
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

    // Kept from HEAD for compatibility
    public List<Camion> getDisponibles(List<LouableFiltre> filtres){
       return getCatalogue(LocalDate.now(), true, filtres);
    }

    // Kept from HEAD for owner management
    public List<Camion> getByProprietaire(int idProprietaire) {
        String sql = "SELECT * FROM LOUABLE l " +
                "JOIN VEHICULE v ON l.id = v.id " +
                "JOIN CAMION ca ON v.id = ca.id " +
                "WHERE l.idProprietaire = ?";
        return jdbcTemplate.query(sql, new Object[]{idProprietaire}, (rs, rowNum) -> new Camion(
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
                rs.getInt("chargeMaxKg"),
                rs.getDouble("volumeUtileM3"),
                rs.getDouble("hauteurM"),
                rs.getDouble("longueurM"),
                rs.getString("permisRequis")
        ));
    }

    public VehiculeSummary toSummary(Camion camion, boolean dispoJour) {
        return new VehiculeSummary(
                new LouableSummary(
                    camion.getIdLouable(),
                    camion.getIdAgent(), // <--- AJOUTER ICI
                    camion.getStatut(),
                    camion.getPrixJour(),
                    camion.getLieuPrincipal(),
                    "Camion",
                    dispoJour
                ),
                camion.getMarque(),
                camion.getModele(),
                camion.getAnnee(),
                camion.getCouleur(),
                camion.getImmatriculation(),
                camion.getKilometrage(),
                "Camion"
        );
    }
}