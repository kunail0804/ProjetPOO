package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.LouableFiltre;
import com.delorent.repository.RepositoryBase;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LouableRepository implements RepositoryBase<LouableSummary, Integer> {

    private final VehiculeRepository vehiculeRepository;

    public LouableRepository(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    public List<LouableSummary> getAll() {
        List<VehiculeSummary> vehicules = vehiculeRepository.getCatalogue(LocalDate.now(), false, List.of());
        List<LouableSummary> res = new ArrayList<>();
        for (VehiculeSummary v : vehicules) res.add(v.louable());
        return res;
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

    public List<LouableSummary> getCatalogue(LocalDate date, boolean uniquementDisponibles, List<LouableFiltre> filtres) {
        List<VehiculeSummary> vehicules = vehiculeRepository.getCatalogue(date, uniquementDisponibles, filtres);
        List<LouableSummary> res = new ArrayList<>();
        for (VehiculeSummary v : vehicules) res.add(v.louable());
        return res;
    }

    public List<LouableSummary> getDisponibles(List<LouableFiltre> filtres) {
        return getCatalogue(LocalDate.now(), true, filtres);
    }
}