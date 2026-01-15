package com.delorent.model.Louable;

import java.time.LocalDate;

public class ControleTechnique {
    
    private Long id;
    private Long vehiculeId;
    private LocalDate dateControle;
    private LocalDate dateValidite;
    private ResultatControle resultat;
    private String centre;
    private Double prix;
    private String commentaires;
    
    // Constructeurs
    public ControleTechnique() {
    }
    
    public ControleTechnique(Long vehiculeId, LocalDate dateControle, 
                            LocalDate dateValidite, ResultatControle resultat, String centre) {
        this.vehiculeId = vehiculeId;
        this.dateControle = dateControle;
        this.dateValidite = dateValidite;
        this.resultat = resultat;
        this.centre = centre;
    }
    
    // Constructeur pour tous les champs
    public ControleTechnique(Long id, Long vehiculeId, LocalDate dateControle, 
                            LocalDate dateValidite, ResultatControle resultat, 
                            String centre, Double prix, String commentaires) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.dateControle = dateControle;
        this.dateValidite = dateValidite;
        this.resultat = resultat;
        this.centre = centre;
        this.prix = prix;
        this.commentaires = commentaires;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getVehiculeId() {
        return vehiculeId;
    }
    
    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }
    
    public LocalDate getDateControle() {
        return dateControle;
    }
    
    public void setDateControle(LocalDate dateControle) {
        this.dateControle = dateControle;
    }
    
    public LocalDate getDateValidite() {
        return dateValidite;
    }
    
    public void setDateValidite(LocalDate dateValidite) {
        this.dateValidite = dateValidite;
    }
    
    public ResultatControle getResultat() {
        return resultat;
    }
    
    public void setResultat(ResultatControle resultat) {
        this.resultat = resultat;
    }
    
    // Méthodes de compatibilité pour les anciens appels qui utilisent String
    public String getResultatString() {
        return resultat != null ? resultat.name() : null;
    }
    
    public void setResultatString(String resultat) {
        if (resultat != null) {
            this.resultat = ResultatControle.valueOf(resultat);
        }
    }
    
    public String getCentre() {
        return centre;
    }
    
    public void setCentre(String centre) {
        this.centre = centre;
    }
    
    public Double getPrix() {
        return prix;
    }
    
    public void setPrix(Double prix) {
        this.prix = prix;
    }
    
    public String getCommentaires() {
        return commentaires;
    }
    
    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }
    
    // Méthode utilitaire pour vérifier si le contrôle est encore valide
    public boolean isValide() {
        return resultat == ResultatControle.VALIDE && 
               dateValidite.isAfter(LocalDate.now());
    }
    
    @Override
    public String toString() {
        return "ControleTechnique [id=" + id + 
               ", vehiculeId=" + vehiculeId + 
               ", dateControle=" + dateControle + 
               ", dateValidite=" + dateValidite + 
               ", resultat=" + resultat + 
               ", centre=" + centre + 
               ", prix=" + prix + 
               ", commentaires=" + commentaires + "]";
    }
}