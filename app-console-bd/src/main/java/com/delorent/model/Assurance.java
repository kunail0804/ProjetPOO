package com.delorent.model;

public class Assurance {
    private Integer idAssurance;
    private String nom;
    private double tarifJournalier;
    
    // Nouveaux champs
    private String cheminFichier;
    private Integer idProprietaire; // Null si c'est une assurance globale, ID de l'agent sinon

    // Constructeur complet (Lecture depuis la BDD)
    public Assurance(Integer idAssurance, String nom, double tarifJournalier, String cheminFichier, Integer idProprietaire) {
        this.idAssurance = idAssurance;
        this.nom = nom;
        this.tarifJournalier = tarifJournalier;
        this.cheminFichier = cheminFichier;
        this.idProprietaire = idProprietaire;
    }

    // Constructeur pour la création (Avant BDD)
    public Assurance(String nom, double tarifJournalier, String cheminFichier, Integer idProprietaire) {
        this.nom = nom;
        this.tarifJournalier = tarifJournalier;
        this.cheminFichier = cheminFichier;
        this.idProprietaire = idProprietaire;
    }

    // Getters et Setters classiques
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