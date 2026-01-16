package com.delorent.model;

public class Assurance {
    private Integer idAssurance;
    private String nom;
    private double tarifJournalier;
    
    private String cheminFichier;
    private Integer idProprietaire;

    public Assurance(Integer idAssurance, String nom, double tarifJournalier, String cheminFichier, Integer idProprietaire) {
        this.idAssurance = idAssurance;
        this.nom = nom;
        this.tarifJournalier = tarifJournalier;
        this.cheminFichier = cheminFichier;
        this.idProprietaire = idProprietaire;
    }

    public Assurance(String nom, double tarifJournalier, String cheminFichier, Integer idProprietaire) {
        this.nom = nom;
        this.tarifJournalier = tarifJournalier;
        this.cheminFichier = cheminFichier;
        this.idProprietaire = idProprietaire;
    }

    public Integer getIdAssurance() { return idAssurance; }
    public void setIdAssurance(Integer idAssurance) { this.idAssurance = idAssurance; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getTarifJournalier() { return tarifJournalier; }
    public void setTarifJournalier(double tarifJournalier) { this.tarifJournalier = tarifJournalier; }

    public String getCheminFichier() { return cheminFichier; }
    public void setCheminFichier(String cheminFichier) { this.cheminFichier = cheminFichier; }

    public Integer getIdProprietaire() { return idProprietaire; }
    public void setIdProprietaire(Integer idProprietaire) { this.idProprietaire = idProprietaire; }
}