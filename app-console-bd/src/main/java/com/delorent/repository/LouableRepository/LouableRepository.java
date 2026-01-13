package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.LouableFiltre;
import com.delorent.repository.RepositoryBase;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate; // Indispensable pour HEAD

// Imports fusionnés (HEAD + US.L.10)
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Repository
public class LouableRepository implements RepositoryBase<LouableSummary, Integer> {

    // On doit avoir les DEUX : VehiculeRepository (US.L.10) et JdbcTemplate (HEAD)
    private final VehiculeRepository vehiculeRepository;
    private final JdbcTemplate jdbcTemplate;

    // Constructeur fusionné : on injecte les deux dépendances
    public LouableRepository(VehiculeRepository vehiculeRepository, JdbcTemplate jdbcTemplate) {
        this.vehiculeRepository = vehiculeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LouableSummary> getAll() {
        // Logique US.L.10 (plus moderne avec getCatalogue)
        List<VehiculeSummary> vehicules = vehiculeRepository.getCatalogue(LocalDate.now(), false, List.of());
        List<LouableSummary> res = new ArrayList<>();
        for (VehiculeSummary v : vehicules) res.add(v.louable());
        return res;
    }

    @Override
    public LouableSummary get(Integer id) {
        // Version HEAD (on garde votre version qui marchait)
        VehiculeSummary vehicule = vehiculeRepository.get(id);
        return vehicule == null ? null : vehicule.louable();
    }

    @Override
    public Integer add(LouableSummary entity) {
        throw new UnsupportedOperationException("Use specific repositories to add vehicles.");
    }

    @Override
    public boolean modify(LouableSummary entity) {
        throw new UnsupportedOperationException("Use specific repositories to update vehicles.");
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Use specific repositories to delete vehicles.");
    }

    // --- Méthodes de la nouvelle branche (US.L.10) ---

    public List<LouableSummary> getCatalogue(LocalDate date, boolean uniquementDisponibles, List<LouableFiltre> filtres) {
        List<VehiculeSummary> vehicules = vehiculeRepository.getCatalogue(date, uniquementDisponibles, filtres);
        List<LouableSummary> res = new ArrayList<>();
        for (VehiculeSummary v : vehicules) res.add(v.louable());
        return res;
    }

    public List<LouableSummary> getDisponibles(List<LouableFiltre> filtres) {
        return getCatalogue(LocalDate.now(), true, filtres);
    }

    // --- Méthodes conservées de la branche HEAD (indispensables pour Parking/Convoyage) ---

    public List<LouableSummary> getByProprietaire(int idProprietaire) {
        List<VehiculeSummary> vehicules = vehiculeRepository.getByProprietaire(idProprietaire);
        List<LouableSummary> summaries = new ArrayList<>();
        for (VehiculeSummary vehicule : vehicules) {
            summaries.add(vehicule.louable());
        }
        return summaries;
    }

    /**
     * Renvoie les idLouable disponibles pour une date donnée (jour).
     * Règle : au moins une disponibilité NON réservée couvrant cette date.
     */
    public Set<Integer> getIdsDisponiblesA(LocalDate date) {
        if (date == null) date = LocalDate.now();

        String sql = """
            SELECT DISTINCT d.idLouable
            FROM DISPONIBILITE d
            WHERE d.estReservee = 0
              AND DATE(d.dateDebut) <= ?
              AND DATE(d.dateFin)   >= ?
        """;

        List<Integer> ids = jdbcTemplate.queryForList(
                sql,
                Integer.class,
                Date.valueOf(date),
                Date.valueOf(date)
        );

        return new HashSet<>(ids);
    }
}