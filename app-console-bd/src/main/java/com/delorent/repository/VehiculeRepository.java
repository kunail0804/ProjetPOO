package com.delorent.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.delorent.model.Vehicule;

@Repository
public class VehiculeRepository {

    private final JdbcTemplate jdbcTemplate;

    public VehiculeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void sauvegarder(Vehicule v) {
        // 1. On insère dans la table parente LOUABLE pour avoir l'ID
        String sqlLouable = "INSERT INTO LOUABLE (idProprietaire, marque, prixJour, statut, lieuPrincipal) VALUES (?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlLouable, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, 1); // On force l'ID 1 (Jean) pour l'instant
            ps.setString(2, v.getMarque());
            ps.setDouble(3, v.getPrixJour());
            ps.setString(4, "DISPONIBLE"); // Statut par défaut
            ps.setString(5, v.getLieuPrincipal());
            return ps;
        }, keyHolder);

        // On récupère l'ID généré par la base de données
        int idGenere = keyHolder.getKey().intValue();
        v.setId(idGenere);

        // 2. On insère le reste dans la table enfant VEHICULE avec le même ID
        String sqlVehicule = "INSERT INTO VEHICULE (id, modele, annee, immatriculation, kilometrage) VALUES (?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sqlVehicule, 
            idGenere, 
            v.getModele(), 
            v.getAnnee(), 
            v.getImmatriculation(), 
            v.getKilometrage()
        );
    }
}