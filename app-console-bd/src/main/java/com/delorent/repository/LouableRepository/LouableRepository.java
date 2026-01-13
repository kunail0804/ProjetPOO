package com.delorent.repository.LouableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.delorent.repository.RepositoryBase;
import com.delorent.model.Louable.LouableFiltre;

// On GARDE ces imports de HEAD car ils sont utilisés par la méthode getIdsDisponiblesA en bas
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@Repository
public class LouableRepository implements RepositoryBase<LouableSummary, Integer> {
    private final JdbcTemplate jdbcTemplate;
    private final VehiculeRepository vehiculeRepository;

    public LouableRepository(JdbcTemplate jdbcTemplate, VehiculeRepository vehiculeRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public List<LouableSummary> getAll() {
        List<VehiculeSummary> vehicules = vehiculeRepository.getAll();
        List<LouableSummary> summaries = new ArrayList<>();
        for (VehiculeSummary vehicule : vehicules) {
            summaries.add(vehicule.louable());
        }
        return summaries;
    }

    @Override
    public LouableSummary get(Integer id) {
        VehiculeSummary vehicule = vehiculeRepository.get(id);
        // On garde la version concise de HEAD
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

    public List<LouableSummary> getDisponibles(List<LouableFiltre> filtres){
        List<VehiculeSummary> vehicules = vehiculeRepository.getDisponibles(filtres);
        List<LouableSummary> summaries = new ArrayList<>();
        for (VehiculeSummary vehicule : vehicules) {
            summaries.add(vehicule.louable());
        }
        return summaries;
    }

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
     * (Code apporté par la branche HEAD)
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