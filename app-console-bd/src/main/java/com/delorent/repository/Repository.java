package com.delorent.repository;

import java.util.List;

public interface Repository<T, ID> {
    List<T> getAll();
    T get(ID id);

    ID add(T entity);
    boolean modify(T entity);
    boolean delete(ID id);
}
