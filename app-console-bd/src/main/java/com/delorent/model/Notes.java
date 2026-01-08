package com.delorent.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Notes {

    private int id;
    private double noteGlobale;
    private String commentaire;
    private Date dateEvaluation;

    private List<Critere> criteres;

    // Constructeur
    public Notes(int id, String commentaire) {
        this.id = id;
        this.commentaire = commentaire;
        this.dateEvaluation = new Date(); 
        this.criteres = new ArrayList<>(); 
    }

    public void calculerNoteGlobale() {
        if (criteres.isEmpty()) {
            this.noteGlobale = 0.0;
            return;
        }

        int somme = 0;
        for (Critere c : criteres) {
            somme += c.getNote();
        }
        this.noteGlobale = somme / criteres.size();
    }

    public void ajouterCritere(Critere critere) {
        this.criteres.add(critere);
        calculerNoteGlobale(); 
    }

    // --- Getters et Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getNoteGlobale() { return noteGlobale; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Date getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(Date dateEvaluation) { this.dateEvaluation = dateEvaluation; }

    public List<Critere> getCriteres() { return criteres; }
    public void setCriteres(List<Critere> criteres) { this.criteres = criteres; }
}