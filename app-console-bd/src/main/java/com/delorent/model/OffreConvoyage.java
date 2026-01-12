package com.delorent.model;

public class OffreConvoyage {
    private int idOffre;
    private int idLouable;
    private int idParkingArrivee;
    private double reduction; // ex: 0.10 pour 10%

    // Attributs pour faciliter l'affichage (optionnel mais pratique)
    private String nomParking;
    private String villeParking;

    public OffreConvoyage() {}

    public OffreConvoyage(int idOffre, int idLouable, int idParkingArrivee, double reduction) {
        this.idOffre = idOffre;
        this.idLouable = idLouable;
        this.idParkingArrivee = idParkingArrivee;
        this.reduction = reduction;
    }

    public int getIdOffre() { return idOffre; }
    public void setIdOffre(int idOffre) { this.idOffre = idOffre; }

    public int getIdLouable() { return idLouable; }
    public void setIdLouable(int idLouable) { this.idLouable = idLouable; }

    public int getIdParkingArrivee() { return idParkingArrivee; }
    public void setIdParkingArrivee(int idParkingArrivee) { this.idParkingArrivee = idParkingArrivee; }

    public double getReduction() { return reduction; }
    public void setReduction(double reduction) { this.reduction = reduction; }

    // Helpers affichage
    public String getNomParking() { return nomParking; }
    public void setNomParking(String nomParking) { this.nomParking = nomParking; }
    public String getVilleParking() { return villeParking; }
    public void setVilleParking(String villeParking) { this.villeParking = villeParking; }
}