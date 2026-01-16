package com.delorent.model;

public class Discussion {
    private int idDiscussion;
    private int idUtilisateur1;
    private int idUtilisateur2;
    private String dateCreation;
    
    private String nomInterlocuteur; 

    public Discussion() {}

    public int getIdDiscussion() { return idDiscussion; }
    public void setIdDiscussion(int idDiscussion) { this.idDiscussion = idDiscussion; }

    public int getIdUtilisateur1() { return idUtilisateur1; }
    public void setIdUtilisateur1(int idUtilisateur1) { this.idUtilisateur1 = idUtilisateur1; }

    public int getIdUtilisateur2() { return idUtilisateur2; }
    public void setIdUtilisateur2(int idUtilisateur2) { this.idUtilisateur2 = idUtilisateur2; }

    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    public String getNomInterlocuteur() { return nomInterlocuteur; }
    public void setNomInterlocuteur(String nomInterlocuteur) { this.nomInterlocuteur = nomInterlocuteur; }
}