package com.delorent.model.Louable;

public enum TypeLouable {
    VOITURE("Voiture"),
    MOTO("Moto"),
    CAMION("Camion");

    private final String label;

    TypeLouable(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
