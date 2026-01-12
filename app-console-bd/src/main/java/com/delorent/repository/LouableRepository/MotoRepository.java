package com.delorent.repository.LouableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.delorent.model.Louable.Moto;
import com.delorent.model.Louable.SqlClause;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.TypeMoto;
import com.delorent.model.Louable.LouableFiltre;

import com.delorent.repository.RepositoryBase;

import java.util.List;
import java.util.ArrayList;

@Repository
public class MotoRepository implements RepositoryBase<Moto, Integer> {
    private final JdbcTemplate jdbcTemplate;

    public MotoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Moto> getAll() {
        String sql = "SELECT * FROM LOUABLE" +
                     " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                     " JOIN MOTO ON VEHICULE.id = MOTO.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Moto(
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
    public Moto get(Integer id) {
        String sql = "SELECT * FROM LOUABLE" +
                     " JOIN VEHICULE ON LOUABLE.id = VEHICULE.id" +
                     " JOIN MOTO ON VEHICULE.id = MOTO.id" +
                     " WHERE LOUABLE.id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new Moto(
                rs.getInt("id"),
                rs.getInt("idProprietaire"),
                rs.getDouble("prixJour"),
                StatutLouable.valueOf(rs.getString("statut")),
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
        String sqlLouable = "INSERT INTO LOUABLE (idProprietaire, prixJour, statut, lieuPrincipal) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlLouable, entity.getIdAgent(), entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal());

        Integer idLouable = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        String sqlVehicule = "INSERT INTO VEHICULE (id, marque, modele, annee, couleur, immatriculation, kilometrage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlVehicule, idLouable, entity.getMarque(), entity.getModele(), entity.getAnnee(), entity.getCouleur(), entity.getImmatriculation(), entity.getKilometrage());

        String sqlMoto = "INSERT INTO MOTO (id, cylindreeCc, puissanceCh, typeMoto, permisRequis) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlMoto, idLouable, entity.getCylindreeCc(), entity.getPuissanceCh(), entity.getTypeMoto().name(), entity.getPermisRequis());

        return idLouable;
    }

    @Override
    public boolean modify(Moto entity) {
        String sqlLouable = "UPDATE LOUABLE SET prixJour = ?, statut = ?, lieuPrincipal = ? WHERE id = ?";
        jdbcTemplate.update(sqlLouable, entity.getPrixJour(), entity.getStatut().name(), entity.getLieuPrincipal(), entity.getIdLouable());

        String sqlVehicule = "UPDATE VEHICULE SET marque = ?, modele = ?, annee = ?, couleur = ?, immatriculation = ?, kilometrage = ? WHERE id = ?";
        jdbcTemplate.update(sqlVehicule, entity.getMarque(), entity.getModele(), entity.getAnnee(), entity.getCouleur(), entity.getImmatriculation(), entity.getKilometrage(), entity.getIdLouable());

        String sqlMoto = "UPDATE MOTO SET cylindreeCc = ?, puissanceCh = ?, typeMoto = ?, permisRequis = ? WHERE id = ?";
        jdbcTemplate.update(sqlMoto, entity.getCylindreeCc(), entity.getPuissanceCh(), entity.getTypeMoto().name(), entity.getPermisRequis(), entity.getIdLouable());

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

    public List<Moto> getDisponibles(List<LouableFiltre> filtres) {
        StringBuilder sql = new StringBuilder("SELECT * FROM LOUABLE l JOIN VEHICULE v ON l.id = v.id JOIN MOTO m ON v.id = m.id WHERE l.statut = 'DISPONIBLE'");
        List<Object> params = new ArrayList<>();

        for (LouableFiltre filtre : filtres) {
            if (filtre.isActif()) {
                SqlClause clause = filtre.toSqlClause();
                sql.append(" AND ").append(clause.getPredicate());
                params.addAll(clause.getParams());
            }
        }
        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> new Moto(
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
}
