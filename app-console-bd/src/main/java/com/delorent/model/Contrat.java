package com.delorent.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Contrat {
    private int id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String lieuPrise;
    private String lieuDepot;

    private BigDecimal prix;
    private String etat;
    
    private int idLoueur;
    private int idLouable;
    private int idAssurance;
    
    private Integer idParkingRetour;

    public Contrat() {
    }

    public Contrat(int id, LocalDate dateDebut, LocalDate dateFin, String lieuPrise, String lieuDepot) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuPrise = lieuPrise;
        this.lieuDepot = lieuDepot;
    }

    public Contrat(int id, LocalDate dateDebut, LocalDate dateFin, String lieuPrise, String lieuDepot, 
                   int idLoueur, int idLouable, int idAssurance, Integer idParkingRetour) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuPrise = lieuPrise;
        this.lieuDepot = lieuDepot;
        this.idLoueur = idLoueur;
        this.idLouable = idLouable;
        this.idAssurance = idAssurance;
        this.idParkingRetour = idParkingRetour;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getLieuPrise() { return lieuPrise; }
    public void setLieuPrise(String lieuPrise) { this.lieuPrise = lieuPrise; }

    public String getLieuDepot() { return lieuDepot; }
    public void setLieuDepot(String lieuDepot) { this.lieuDepot = lieuDepot; }

    public int getIdLoueur() { return idLoueur; }
    public void setIdLoueur(int idLoueur) { this.idLoueur = idLoueur; }

    public int getIdLouable() { return idLouable; }
    public void setIdLouable(int idLouable) { this.idLouable = idLouable; }

    public int getIdAssurance() { return idAssurance; }
    public void setIdAssurance(int idAssurance) { this.idAssurance = idAssurance; }

    public Integer getIdParkingRetour() { return idParkingRetour; }
    public void setIdParkingRetour(Integer idParkingRetour) { this.idParkingRetour = idParkingRetour; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
}