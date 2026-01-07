package com.delorent.model;


public abstract class Louable {

    private int id;
    private String marque;
    private String modele;
    private int annee;
    private String couleur;
    private double prixJour;
    
    private StatutLouable statut;
    
    private String lieuPrincipal;

    public Louable(int id, String marque, String modele, double prixJour) {
        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.prixJour = prixJour;
        this.statut = StatutLouable.DISPONIBLE;
    }

    public int getId() {
        return id;
    }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public int getAnnee() {
        return annee;
    }

    public String getCouleur() {
        return couleur;
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

    public void setModele(String modele) {
        this.modele = modele;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
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