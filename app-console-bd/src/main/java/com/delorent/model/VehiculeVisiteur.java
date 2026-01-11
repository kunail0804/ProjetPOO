package com.delorent.model;

import java.time.LocalDateTime;

public class VehiculeVisiteur {

    private String type;
    private String marque;
    private String modele;
    private String couleur;
    private String lieu;

    private LocalDateTime dateDebutDisponibilite;
    private LocalDateTime dateFinDisponibilite;

    private Double noteGlobale;

    private int idLouable;

    public int getIdLouable() {
        return idLouable;
    }

    public void setIdLouable(int idLouable) {
        this.idLouable = idLouable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDateTime getDateDebutDisponibilite() {
        return dateDebutDisponibilite;
    }

    public void setDateDebutDisponibilite(LocalDateTime dateDebutDisponibilite) {
        this.dateDebutDisponibilite = dateDebutDisponibilite;
    }

    public LocalDateTime getDateFinDisponibilite() {
        return dateFinDisponibilite;
    }

    public void setDateFinDisponibilite(LocalDateTime dateFinDisponibilite) {
        this.dateFinDisponibilite = dateFinDisponibilite;
    }

    public Double getNoteGlobale() {
        return noteGlobale;
    }

    public void setNoteGlobale(Double noteGlobale) {
        this.noteGlobale = noteGlobale;
    }
}