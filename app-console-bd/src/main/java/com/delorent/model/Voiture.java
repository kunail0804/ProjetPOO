package com.delorent.model;

public class Voiture extends Vehicule {

    private int nbPortes;
    private int nbPlaces;
    private int volumeCoffreLitres;
    private TypeBoite boite;     // Utilise l'Enum créé plus haut
    private Carburant carburant; // Utilise l'Enum créé plus haut
    private boolean climatisation;
    
    public Voiture(int id, String marque, String modele, double prixJour, String immatriculation, int nbPortes,
            int nbPlaces, int volumeCoffreLitres, TypeBoite boite, Carburant carburant, boolean climatisation) {
        super(id, marque, modele, prixJour, immatriculation);
        this.nbPortes = nbPortes;
        this.nbPlaces = nbPlaces;
        this.volumeCoffreLitres = volumeCoffreLitres;
        this.boite = boite;
        this.carburant = carburant;
        this.climatisation = climatisation;
    }
    public int getNbPortes() {
        return nbPortes;
    }
    public int getNbPlaces() {
        return nbPlaces;
    }
    public int getVolumeCoffreLitres() {
        return volumeCoffreLitres;
    }
    public TypeBoite getBoite() {
        return boite;
    }
    public Carburant getCarburant() {
        return carburant;
    }
    public boolean isClimatisation() {
        return climatisation;
    }

    public void setNbPortes(int nbPortes) {
        this.nbPortes = nbPortes;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public void setVolumeCoffreLitres(int volumeCoffreLitres) {
        this.volumeCoffreLitres = volumeCoffreLitres;
    }

    public void setBoite(TypeBoite boite) {
        this.boite = boite;
    }

    public void setCarburant(Carburant carburant) {
        this.carburant = carburant;
    }

    public void setClimatisation(boolean climatisation) {
        this.climatisation = climatisation;
    }

    
}