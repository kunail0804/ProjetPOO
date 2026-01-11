package com.delorent.repository.LouableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.delorent.repository.RepositoryBase;
import com.delorent.model.Louable.LouableFiltre;
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
        return vehicule.louable();
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
}
