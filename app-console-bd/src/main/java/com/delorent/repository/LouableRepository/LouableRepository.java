package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.LouableFiltre;
import com.delorent.repository.RepositoryBase;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Repository
public class LouableRepository implements RepositoryBase<LouableSummary, Integer> {

    private final VehiculeRepository vehiculeRepository;
    private final JdbcTemplate jdbcTemplate;

    public LouableRepository(VehiculeRepository vehiculeRepository, JdbcTemplate jdbcTemplate) {
        this.vehiculeRepository = vehiculeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<LouableSummary> getAll() {
        List<LouableSummary> res = new ArrayList<>();
        List<VehiculeSummary> vehicules = vehiculeRepository.getAll();
        for (VehiculeSummary v : vehicules) res.add(v.louable());
        return res;
    }

    @Override
    public LouableSummary get(Integer id) {
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

    public List<LouableSummary> getDisponibles(List<LouableFiltre> filtres) {
        List<VehiculeSummary> vehicules = vehiculeRepository.getDisponibles(filtres);
        List<LouableSummary> res = new ArrayList<>();
        for (VehiculeSummary v : vehicules) res.add(v.louable());
        return res;
    }

    public List<LouableSummary> getByProprietaire(int idProprietaire) {
        List<VehiculeSummary> vehicules = vehiculeRepository.getByProprietaire(idProprietaire);
        List<LouableSummary> summaries = new ArrayList<>();
        for (VehiculeSummary vehicule : vehicules) {
            summaries.add(vehicule.louable());
        }
        return summaries;
    }
}