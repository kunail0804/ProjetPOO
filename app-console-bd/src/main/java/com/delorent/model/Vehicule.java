package com.delorent.model;

public abstract class Vehicule extends Louable {

    private String modele;
    private int annee;
    private String couleur;
    private String immatriculation;
    private int kilometrage;

    // Constructeur (optionnel, à adapter selon vos besoins)
    public Vehicule(int id, int idProprietaire, String marque, double prixJour, String modele, int annee, String couleur, String immatriculation) {
        super(id, idProprietaire, marque, prixJour); // Appel au constructeur de Louable
        this.modele = modele;
        this.annee = annee;
        this.couleur = couleur;
        this.immatriculation = immatriculation;
    }

    // Getters et Setters
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public int getKilometrage() { return kilometrage; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }

    public String getModele() {return modele;}
    public void setModele(String modele) {this.modele = modele;}

    public int getAnnee() {return annee;}
    public void setAnnee(int annee) {this.annee = annee;}

    public String getCouleur() {return couleur;}        
    public void setCouleur(String couleur) {this.couleur = couleur;}   
}