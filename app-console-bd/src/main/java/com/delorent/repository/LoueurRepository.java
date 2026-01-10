package com.delorent.repository;

import org.springframework.stereotype.Repository;
import com.delorent.model.Loueur;

@Repository
public class LoueurRepository implements RepositoryBase<Loueur, Long> {

    @Override
    public java.util.List<Loueur> getAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Loueur get(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Long add(Loueur entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean modify(Loueur entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
