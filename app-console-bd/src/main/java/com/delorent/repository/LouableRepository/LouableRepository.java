package com.delorent.repository.LouableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.delorent.repository.RepositoryBase;
import com.delorent.model.Louable.LouableFiltre;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

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
        // Ancien comportement : sans notion de date => on prend "aujourd'hui" et pas de filtre "uniquement dispo"
        return getCatalogue(LocalDate.now(), false, List.of());
    }

    @Override
    public LouableSummary get(Integer id) {
        VehiculeSummary v = vehiculeRepository.get(id);
        return v == null ? null : v.louable();
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

    public List<LouableSummary> getCatalogue(LocalDate dateCible, boolean uniquementDisponibles, List<LouableFiltre> filtres) {
        List<VehiculeSummary> vehicules = vehiculeRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);
        List<LouableSummary> summaries = new ArrayList<>();
        for (VehiculeSummary v : vehicules) {
            summaries.add(v.louable());
        }
        return summaries;
    }
}