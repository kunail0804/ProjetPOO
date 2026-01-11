package com.delorent.model;

import java.time.LocalDate;

public class Contrat {

    private int id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieuPrise;
    private String lieuDepot;
    //private double prixEstime;
    //private ContratEtat etat;

    public Contrat(int id, LocalDate dateDebut, LocalDate dateFin, String lieuPrise, String lieuDepot) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuPrise = lieuPrise;
        this.lieuDepot = lieuDepot;
    }

    public Contrat(LocalDate dateDebut, LocalDate dateFin, String lieuPrise, String lieuDepot) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuPrise = lieuPrise;
        this.lieuDepot = lieuDepot;
    }

    public int getId() { return id; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public String getLieuPrise() { return lieuPrise; }
    public String getLieuDepot() { return lieuDepot; }


    public void setId(int id) { this.id = id; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin;}
    public void setLieuPrise(String lieuPrise) { this.lieuPrise = lieuPrise; }
    public void setLieuDepot(String lieuDepot) { this.lieuDepot = lieuDepot;}

}