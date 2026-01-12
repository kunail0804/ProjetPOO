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

import java.util.List;
import java.util.ArrayList;

@Repository
public class VoitureRepository implements RepositoryBase<Voiture,Integer> {
    private final JdbcTemplate jdbcTemplate;

    public VoitureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Voiture> getAll() {
        String sql = "SELECT * FROM LOUABLE" +
                     " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                     " JOIN VOITURE ON VEHICULE.id = VOITURE.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Voiture(
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
        String sqlLouable = "INSERT INTO LOUABLE (idProprietaire, prixJour, statut, lieuPrincipal) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlLouable, entity.getIdAgent(), entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal());

        Integer idLouable = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        String sqlVehicule = "INSERT INTO VEHICULE (id, marque, modele, annee, couleur, immatriculation, kilometrage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlVehicule, idLouable, entity.getMarque(), entity.getModele(), entity.getAnnee(), entity.getCouleur(), entity.getImmatriculation(), entity.getKilometrage());

        String sqlVoiture = "INSERT INTO VOITURE (id, nbPortes, nbPlaces, volumeCoffreLitres, boite, carburant, climatisation) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlVoiture, idLouable, entity.getNbPortes(), entity.getNbPlaces(), entity.getVolumeCoffreLitres(), entity.getBoite().name(), entity.getCarburant().name(), entity.isClimatisation());

        return idLouable;
    }

    @Override
    public boolean modify(Voiture entity) {
        String sqlLouable = "UPDATE LOUABLE SET prixJour = ?, statut = ?, lieuPrincipal = ? WHERE id = ?";
        int rowsLouable = jdbcTemplate.update(sqlLouable, entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal(), entity.getIdLouable());

        String sqlVehicule = "UPDATE VEHICULE SET marque = ?, modele = ?, annee = ?, couleur = ?, immatriculation = ?, kilometrage = ? WHERE id = ?";
        int rowsVehicule = jdbcTemplate.update(sqlVehicule, entity.getMarque(), entity.getModele(), entity.getAnnee(), entity.getCouleur(), entity.getImmatriculation(), entity.getKilometrage(), entity.getIdLouable());

        String sqlVoiture = "UPDATE VOITURE SET nbPortes = ?, nbPlaces = ?, volumeCoffreLitres = ?, boite = ?, carburant = ?, climatisation = ? WHERE id = ?";
        int rowsVoiture = jdbcTemplate.update(sqlVoiture, entity.getNbPortes(), entity.getNbPlaces(), entity.getVolumeCoffreLitres(), entity.getBoite().name(), entity.getCarburant().name(), entity.isClimatisation(), entity.getIdLouable());

        return (rowsLouable > 0) && (rowsVehicule > 0) && (rowsVoiture > 0);
    }

    @Override
    public boolean delete(Integer id) {
        String sqlVoiture = "DELETE FROM VOITURE WHERE id = ?";
        int rowsVoiture = jdbcTemplate.update(sqlVoiture, id);

        String sqlVehicule = "DELETE FROM VEHICULE WHERE id = ?";
        int rowsVehicule = jdbcTemplate.update(sqlVehicule, id);

        String sqlLouable = "DELETE FROM LOUABLE WHERE id = ?";
        int rowsLouable = jdbcTemplate.update(sqlLouable, id);

        return (rowsVoiture > 0) && (rowsVehicule > 0) && (rowsLouable > 0);
    }

    public List<Voiture> getDisponibles(List<LouableFiltre> filtres) {
        StringBuilder sql = new StringBuilder("SELECT * FROM LOUABLE l JOIN VEHICULE v ON l.id = v.id JOIN VOITURE vo ON v.id = vo.id WHERE l.statut = 'DISPONIBLE'");
        List<Object> params = new ArrayList<>();

        for (LouableFiltre filtre : filtres) {
            if (filtre.isActif()) {
                SqlClause clause = filtre.toSqlClause();
                sql.append(" AND ").append(clause.getPredicate());
                params.addAll(clause.getParams());
            }
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> new Voiture(
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
                rs.getInt("nbPortes"),
                rs.getInt("nbPlaces"),
                rs.getInt("volumeCoffreLitres"),
                TypeBoite.valueOf(rs.getString("boite").toUpperCase()),
                Carburant.valueOf(rs.getString("carburant").toUpperCase()),
                rs.getBoolean("climatisation")
        ));
    }

    public List<Voiture> getByProprietaire(int idProprietaire) {
        String sql = "SELECT * FROM LOUABLE l" +
                     " JOIN VEHICULE v ON l.id = v.id" +
                     " JOIN VOITURE vo ON v.id = vo.id" +
                     " WHERE l.idProprietaire = ?";
        return jdbcTemplate.query(sql, new Object[]{idProprietaire}, (rs, rowNum) -> new Voiture(
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
                rs.getInt("nbPortes"),
                rs.getInt("nbPlaces"),
                rs.getInt("volumeCoffreLitres"),
                TypeBoite.valueOf(rs.getString("boite").toUpperCase()),
                Carburant.valueOf(rs.getString("carburant").toUpperCase()),
                rs.getBoolean("climatisation")
        ));
    }
}
