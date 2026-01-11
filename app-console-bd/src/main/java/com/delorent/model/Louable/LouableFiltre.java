package com.delorent.model.Louable;

public interface LouableFiltre {
    String libelle();
    boolean isActif();
    SqlClause toSqlClause();
}