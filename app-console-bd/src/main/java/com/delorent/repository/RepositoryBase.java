package com.delorent.repository;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RepositoryBase<T, ID> {
    List<T> getAll();
    T get(ID id);

    ID add(T entity);
    boolean modify(T entity);
    boolean delete(ID id);
}
