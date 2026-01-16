package com.delorent.model;

public class Critere {

    private int id;
    private String libelle;
    private int note;

    public Critere(int id, String libelle, int note) {
        this.id = id;
        this.libelle = libelle;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    
}