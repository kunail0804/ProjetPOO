// FICHIER: src/main/java/com/delorent/repository/LouableRepository/CamionRepository.java
package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.Camion;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.model.Louable.SqlClause;
import com.delorent.repository.RepositoryBase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Repository
public class CamionRepository implements RepositoryBase<Camion, Integer> {
    private final JdbcTemplate jdbcTemplate;

    public CamionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Camion> getAll() {
        String sql = "SELECT * FROM LOUABLE" +
                " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                " JOIN CAMION ON VEHICULE.id = CAMION.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Camion(
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
        String sqlCamion = "DELETE FROM CAMION WHERE id = ?";
        jdbcTemplate.update(sqlCamion, id);

        String sqlVehicule = "DELETE FROM VEHICULE WHERE id = ?";
        jdbcTemplate.update(sqlVehicule, id);

        String sqlLouable = "DELETE FROM LOUABLE WHERE id = ?";
        jdbcTemplate.update(sqlLouable, id);

        return true;
    }

    /**
     * CHANGEMENT :
     * Avant: WHERE l.statut = 'DISPONIBLE'
     * Maintenant: on renvoie TOUT, et l'UI affiche DISPONIBLE si dispo aujourd'hui.
     */
    public List<Camion> getDisponibles(List<LouableFiltre> filtres){
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM LOUABLE l " +
                        "JOIN VEHICULE v ON l.id = v.id " +
                        "JOIN CAMION ca ON v.id = ca.id " +
                        "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        for (LouableFiltre filtre : filtres) {
            if (filtre.isActif()) {
                SqlClause clause = filtre.toSqlClause();
                sql.append(" AND ").append(clause.getPredicate());
                params.addAll(clause.getParams());
            }
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> new Camion(
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
}