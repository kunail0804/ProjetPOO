package com.delorent.model;

import java.time.LocalDate;

public class Contrat {

    private final LocalDate dateDebut;
    private final LocalDate dateFin;
    private final String lieuPrise;
    private final String lieuDepot;
    private final double prixEstime;

    public Contrat(LocalDate dateDebut, LocalDate dateFin, String lieuPrise, String lieuDepot, double prixEstime) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuPrise = lieuPrise;
        this.lieuDepot = lieuDepot;
        this.prixEstime = prixEstime;
    }

    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public String getLieuPrise() { return lieuPrise; }
    public String getLieuDepot() { return lieuDepot; }
    public double getPrixEstime() { return prixEstime; }
}