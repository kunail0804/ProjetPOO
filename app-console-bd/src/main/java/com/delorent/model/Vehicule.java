package com.delorent.model;

public abstract class Vehicule extends Louable {

    private String immatriculation;
    private int kilometrage;

    // Constructeur (optionnel, à adapter selon vos besoins)
    public Vehicule(int id, String marque, String modele, double prixJour, String immatriculation) {
        super(id, marque, modele, prixJour); // Appel au constructeur de Louable
        this.immatriculation = immatriculation;
    }

    // Getters et Setters
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }

    public int getKilometrage() { return kilometrage; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }
}