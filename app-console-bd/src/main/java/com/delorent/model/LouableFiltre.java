package com.delorent.model;

public interface LouableFiltre {
    String libelle();
    boolean isActif();
    SqlClause toSqlClause();
}