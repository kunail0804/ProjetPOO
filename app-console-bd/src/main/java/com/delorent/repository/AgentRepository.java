package com.delorent.repository;

import com.delorent.model.Agent;

public class AgentRepository implements Repository<Agent, Long> {

    @Override
    public java.util.List<Agent> getAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Agent get(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Long add(Agent entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean modify(Agent entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean delete(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
