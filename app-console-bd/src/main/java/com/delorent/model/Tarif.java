package com.delorent.model;

public class Tarif {
    private int idTarif;
    private int idEntreprise;
    private String typeVehicule;
    private String modele;
    private double prixForfait;

    public Tarif() {}

    // Getters et Setters
    public int getIdTarif() { return idTarif; }
    public void setIdTarif(int idTarif) { this.idTarif = idTarif; }

    public int getIdEntreprise() { return idEntreprise; }
    public void setIdEntreprise(int idEntreprise) { this.idEntreprise = idEntreprise; }

    public String getTypeVehicule() { return typeVehicule; }
    public void setTypeVehicule(String typeVehicule) { this.typeVehicule = typeVehicule; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public double getPrixForfait() { return prixForfait; }
    public void setPrixForfait(double prixForfait) { this.prixForfait = prixForfait; }
}