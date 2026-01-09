package com.delorent.repository;

import com.delorent.model.Utilisateur;

public class UtilisateurRepository implements Repository<Utilisateur, Long> {

    @Override
    public java.util.List<Utilisateur> getAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Utilisateur get(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Long add(Utilisateur entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean modify(Utilisateur entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}