package com.delorent.dao;

import com.delorent.model.VehiculeVisiteur;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VehiculeVisiteurDao {

    private final JdbcTemplate jdbcTemplate;

    public VehiculeVisiteurDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // uniquement disponibles
    public List<VehiculeVisiteur> findVehiculesDisponibles() {

        String sql = """
            SELECT 
                l.id,
                l.marque,
                v.modele,
                v.couleur,
                l.lieuPrincipal,
                d.dateDebut,
                d.dateFin,
                l.statut,
                CASE 
                    WHEN vo.id IS NOT NULL THEN 'Voiture'
                    WHEN m.id IS NOT NULL THEN 'Moto'
                    WHEN c.id IS NOT NULL THEN 'Camion'
                    ELSE 'Véhicule'
                END AS typeVehicule
            FROM LOUABLE l
            JOIN VEHICULE v ON v.id = l.id
            JOIN DISPONIBILITE d ON d.idLouable = l.id
            LEFT JOIN VOITURE vo ON vo.id = l.id
            LEFT JOIN MOTO m ON m.id = l.id
            LEFT JOIN CAMION c ON c.id = l.id
            WHERE d.estReservee = 0
              AND l.statut = 'DISPONIBLE'
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapVehicule(rs));
    }

    // tous les véhicules
    public List<VehiculeVisiteur> findTousLesVehicules() {

        String sql = """
            SELECT 
                l.id,
                l.marque,
                v.modele,
                v.couleur,
                l.lieuPrincipal,
                d.dateDebut,
                d.dateFin,
                l.statut,
                CASE 
                    WHEN vo.id IS NOT NULL THEN 'Voiture'
                    WHEN m.id IS NOT NULL THEN 'Moto'
                    WHEN c.id IS NOT NULL THEN 'Camion'
                    ELSE 'Véhicule'
                END AS typeVehicule
            FROM LOUABLE l
            JOIN VEHICULE v ON v.id = l.id
            LEFT JOIN DISPONIBILITE d ON d.idLouable = l.id
            LEFT JOIN VOITURE vo ON vo.id = l.id
            LEFT JOIN MOTO m ON m.id = l.id
            LEFT JOIN CAMION c ON c.id = l.id
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapVehicule(rs));
    }

    //Mapping centralisé
    private VehiculeVisiteur mapVehicule(java.sql.ResultSet rs) throws java.sql.SQLException {
        VehiculeVisiteur v = new VehiculeVisiteur();
        v.setIdLouable(rs.getInt("id"));
        v.setType(rs.getString("typeVehicule"));
        v.setMarque(rs.getString("marque"));
        v.setModele(rs.getString("modele"));
        v.setCouleur(rs.getString("couleur"));
        v.setLieu(rs.getString("lieuPrincipal"));

        if (rs.getTimestamp("dateDebut") != null) {
            v.setDateDebutDisponibilite(
                    rs.getTimestamp("dateDebut").toLocalDateTime()
            );
        }
        if (rs.getTimestamp("dateFin") != null) {
            v.setDateFinDisponibilite(
                    rs.getTimestamp("dateFin").toLocalDateTime()
            );
        }

        return v;
    }
}
