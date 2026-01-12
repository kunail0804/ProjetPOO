package com.delorent.model.Louable;

public class Voiture extends Vehicule {

    private int nbPortes;
    private int nbPlaces;
    private int volumeCoffreLitres;
    private TypeBoite boite;
    private Carburant carburant;
    private boolean climatisation;
    
    public Voiture(int idLouable, int idAgent, double prixJour, String lieuPrincipal, String marque, String modele, int annee,
            String couleur, String immatriculation, int kilometrage, int nbPortes, int nbPlaces, int volumeCoffreLitres,
            TypeBoite boite, Carburant carburant, boolean climatisation) {
        super(idLouable, idAgent, prixJour, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.nbPortes = nbPortes;
        this.nbPlaces = nbPlaces;
        this.volumeCoffreLitres = volumeCoffreLitres;
        this.boite = boite;
        this.carburant = carburant;
        this.climatisation = climatisation;
    }
    public Voiture(int idAgent, double prixJour, String lieuPrincipal, String marque, String modele, int annee, String couleur,
            String immatriculation, int kilometrage, int nbPortes, int nbPlaces, int volumeCoffreLitres,
            TypeBoite boite, Carburant carburant, boolean climatisation) {
        super(idAgent, prixJour, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.nbPortes = nbPortes;
        this.nbPlaces = nbPlaces;
        this.volumeCoffreLitres = volumeCoffreLitres;
        this.boite = boite;
        this.carburant = carburant;
        this.climatisation = climatisation;
    }
    public Voiture(int idLouable, int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal, String marque,
            String modele, int annee, String couleur, String immatriculation, int kilometrage, int nbPortes,
            int nbPlaces, int volumeCoffreLitres, TypeBoite boite, Carburant carburant, boolean climatisation) {
        super(idLouable, idAgent, prixJour, statut, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.nbPortes = nbPortes;
        this.nbPlaces = nbPlaces;
        this.volumeCoffreLitres = volumeCoffreLitres;
        this.boite = boite;
        this.carburant = carburant;
        this.climatisation = climatisation;
    }
    public Voiture(int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal, String marque, String modele, int annee,
            String couleur, String immatriculation, int kilometrage, int nbPortes, int nbPlaces, int volumeCoffreLitres,
            TypeBoite boite, Carburant carburant, boolean climatisation) {
        super(idAgent, prixJour, statut, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
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