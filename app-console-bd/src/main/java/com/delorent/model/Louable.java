package com.delorent.model;


public abstract class Louable {

    private int idLouable;
    private double prixJour;
    private StatutLouable statut;
    private String lieuPrincipal;

    public Louable(int idLouable, double prixJour, String lieuPrincipal) {
        this.idLouable = idLouable;
        this.prixJour = prixJour;
        this.statut = StatutLouable.INDISPONIBLE;
        this.lieuPrincipal = lieuPrincipal;
    }

    public Louable(double prixJour, String lieuPrincipal) {
        this.prixJour = prixJour;
        this.statut = StatutLouable.INDISPONIBLE;
        this.lieuPrincipal = lieuPrincipal;
    }

    public Louable(int idLouable, double prixJour, StatutLouable statut, String lieuPrincipal) {
        this.idLouable = idLouable;
        this.prixJour = prixJour;
        this.statut = statut;
        this.lieuPrincipal = lieuPrincipal;
    }

    public Louable(double prixJour, StatutLouable statut, String lieuPrincipal) {
        this.prixJour = prixJour;
        this.statut = statut;
        this.lieuPrincipal = lieuPrincipal;
    }

     // Getters et Setters

    public int getIdLouable() {
        return idLouable;
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

    public void setIdLouable(int idLouable) {
        this.idLouable = idLouable;
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