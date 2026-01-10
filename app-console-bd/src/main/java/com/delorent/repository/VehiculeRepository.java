package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Repository pour gérer les véhicules
 * VERSION TEST : utilise des données fictives pour démo
 */
@Repository
public class VehiculeRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public VehiculeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        System.out.println("✅ VehiculeRepository créé - Prêt à utiliser");
    }
    
    /**
     * VERSION TEST : Retourne des véhicules fictifs
     * TODO : Remplacer par vraie requête SQL de la BDD
     */
    public List<Map<String, Object>> findVehiculesDisponibles() {
        System.out.println("⚠️  Utilisation données TEST (BDD non connectée)");
        
        List<Map<String, Object>> vehicules = new ArrayList<>();
        
        // Véhicule 1 fictif
        Map<String, Object> v1 = new LinkedHashMap<>();
        v1.put("id", 1L);
        v1.put("type", "Voiture");
        v1.put("marque", "Peugeot");
        v1.put("modele", "208");
        v1.put("couleur", "Bleu");
        v1.put("lieu", "Toulouse");
        v1.put("noteGlobale", 4.5);
        v1.put("dateDebutDisponibilite", Date.valueOf(LocalDate.now()));
        v1.put("dateFinDisponibilite", Date.valueOf(LocalDate.now().plusDays(30)));
        v1.put("statut", "DISPONIBLE");
        vehicules.add(v1);
        
        // Véhicule 2 fictif
        Map<String, Object> v2 = new LinkedHashMap<>();
        v2.put("id", 2L);
        v2.put("type", "Voiture");
        v2.put("marque", "Renault");
        v2.put("modele", "Clio");
        v2.put("couleur", "Rouge");
        v2.put("lieu", "Bordeaux");
        v2.put("noteGlobale", 3.8);
        v2.put("dateDebutDisponibilite", Date.valueOf(LocalDate.now()));
        v2.put("dateFinDisponibilite", Date.valueOf(LocalDate.now().plusDays(15)));
        v2.put("statut", "DISPONIBLE");
        vehicules.add(v2);
        
        return vehicules;
    }
    
    /**
     * VERSION TEST : Retourne tous les véhicules
     */
    public List<Map<String, Object>> findAllVehicules() {
        // Pour le test, mêmes données
        return findVehiculesDisponibles();
    }
}


    

