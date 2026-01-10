package com.delorent.controller;

public class DemandeLocation {
    private int idLoueur;
    private int idLouable;
    private int idAssurance;
    private java.time.LocalDate dateDebut;
    private java.time.LocalDate dateFin;
    private String lieuDepotOptionnel;

    public DemandeLocation(int idLoueur, int idLouable, int idAssurance, java.time.LocalDate dateDebut, java.time.LocalDate dateFin, String lieuDepotOptionnel) {
        this.idLoueur = idLoueur;
        this.idLouable = idLouable;
        this.idAssurance = idAssurance;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuDepotOptionnel = lieuDepotOptionnel;
    }

    public int getIdLoueur() {
        return idLoueur;
    }

    public int getIdLouable() {
        return idLouable;
    }

    public int getIdAssurance() {
        return idAssurance;
    }

    public java.time.LocalDate getDateDebut() {
        return dateDebut;
    }

    public java.time.LocalDate getDateFin() {
        return dateFin;
    }

    public String getLieuDepotOptionnel() {
        return lieuDepotOptionnel;
    }

}
