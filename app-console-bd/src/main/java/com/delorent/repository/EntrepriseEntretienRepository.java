package com.delorent.repository;

import org.springframework.stereotype.Repository;
import com.delorent.model.EntrepriseEntretien;

@Repository
public class EntrepriseEntretienRepository implements RepositoryBase<EntrepriseEntretien, Long> {

    @Override
    public java.util.List<EntrepriseEntretien> getAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public EntrepriseEntretien get(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Long add(EntrepriseEntretien entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean modify(EntrepriseEntretien entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
