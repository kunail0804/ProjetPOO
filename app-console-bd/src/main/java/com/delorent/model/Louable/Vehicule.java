package com.delorent.model.Louable;

public abstract class Vehicule extends Louable {

    private String marque;
    private String modele;
    private int annee;
    private String couleur;
    private String immatriculation;
    private int kilometrage;

    public Vehicule(int idLouable, int idAgent, double prixJour, String lieuPrincipal, String marque, String modele, int annee,
            String couleur, String immatriculation, int kilometrage) {
        super(idLouable, idAgent, prixJour, lieuPrincipal);
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.couleur = couleur;
        this.immatriculation = immatriculation;
        this.kilometrage = kilometrage;
    }
    public Vehicule(int idAgent, double prixJour, String lieuPrincipal, String marque, String modele, int annee, String couleur,
            String immatriculation, int kilometrage) {
        super(idAgent, prixJour, lieuPrincipal);
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.couleur = couleur;
        this.immatriculation = immatriculation;
        this.kilometrage = kilometrage;
    }
    public Vehicule(int idLouable, int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal, String marque,
            String modele, int annee, String couleur, String immatriculation, int kilometrage) {
        super(idLouable, idAgent, prixJour, statut, lieuPrincipal);
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.couleur = couleur;
        this.immatriculation = immatriculation;
        this.kilometrage = kilometrage;
    }
    public Vehicule(int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal, String marque, String modele,
            int annee, String couleur, String immatriculation, int kilometrage) {
        super(idAgent, prixJour, statut, lieuPrincipal);
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.couleur = couleur;
        this.immatriculation = immatriculation;
        this.kilometrage = kilometrage;
    }
    public String getMarque() {
        return marque;
    }
    public void setMarque(String marque) {
        this.marque = marque;
    }
    public String getModele() {
        return modele;
    }
    public void setModele(String modele) {
        this.modele = modele;
    }
    public int getAnnee() {
        return annee;
    }
    public void setAnnee(int annee) {
        this.annee = annee;
    }
    public String getCouleur() {
        return couleur;
    }
    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
    public String getImmatriculation() {
        return immatriculation;
    }
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }
    public int getKilometrage() {
        return kilometrage;
    }
    public void setKilometrage(int kilometrage) {
        this.kilometrage = kilometrage;
    }
}
    