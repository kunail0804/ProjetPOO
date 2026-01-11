package com.delorent.model;

public abstract class AbstractLouableFiltre<T> implements LouableFiltre {

    private final String libelle;
    private final T valeur;

    protected AbstractLouableFiltre(String libelle, T valeur) {
        this.libelle = libelle;
        this.valeur = valeur;
    }

    @Override
    public String libelle() {
        return libelle;
    }

    protected T valeur() {
        return valeur;
    }
}
