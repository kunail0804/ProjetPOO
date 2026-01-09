package com.delorent.model;


public abstract class Louable {

    private int id;
    private int idProprietaire;
    private String marque;
    private double prixJour;
    
    private StatutLouable statut;
    
    private String lieuPrincipal;

    public Louable(int id, int idProprietaire, String marque, double prixJour) {
        this.id = id;
        this.idProprietaire = idProprietaire;
        this.marque = marque;
        this.prixJour = prixJour;
        this.statut = StatutLouable.DISPONIBLE;
    }

    public int getId() {
        return id;
    }

    public int getIdProprietaire() {
        return idProprietaire;
    }

    public String getMarque() {
        return marque;
    }

    public double getPrixJour() {
        return prixJour;
    }

    public StatutLouable getStatut() {
        return statut;
    }

    public String getLieuPrincipal() {
        return lieuPrincipal;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public void setPrixJour(double prixJour) {
        this.prixJour = prixJour;
    }

    public void setStatut(StatutLouable statut) {
        this.statut = statut;
    }

    public void setLieuPrincipal(String lieuPrincipal) {
        this.lieuPrincipal = lieuPrincipal;
    }

}