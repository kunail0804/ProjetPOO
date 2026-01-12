package com.delorent.model;

public class Parking {
    private int idParking;
    private String nom;
    private String ville;
    private String adresse;
    private double prixAgent;

    public Parking() {}

    public Parking(int idParking, String nom, String ville, String adresse, double prixAgent) {
        this.idParking = idParking;
        this.nom = nom;
        this.ville = ville;
        this.adresse = adresse;
        this.prixAgent = prixAgent;
    }

    public int getIdParking() { return idParking; }
    public void setIdParking(int idParking) { this.idParking = idParking; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    
    public double getPrixAgent() { return prixAgent; }
    public void setPrixAgent(double prixAgent) { this.prixAgent = prixAgent; }
}