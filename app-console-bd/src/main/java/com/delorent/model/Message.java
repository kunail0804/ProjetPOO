package com.delorent.model;

public class Message {
    private int idMessage;
    private int idDiscussion;
    private int idExpediteur;
    private String contenu;
    private String dateHeure;

    public Message() {}

    public int getIdMessage() { return idMessage; }
    public void setIdMessage(int idMessage) { this.idMessage = idMessage; }

    public int getIdDiscussion() { return idDiscussion; }
    public void setIdDiscussion(int idDiscussion) { this.idDiscussion = idDiscussion; }

    public int getIdExpediteur() { return idExpediteur; }
    public void setIdExpediteur(int idExpediteur) { this.idExpediteur = idExpediteur; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getDateHeure() { return dateHeure; }
    public void setDateHeure(String dateHeure) { this.dateHeure = dateHeure; }
}