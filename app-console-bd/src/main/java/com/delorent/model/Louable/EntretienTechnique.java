package com.delorent.model.Louable;

import java.time.LocalDate;

public class EntretienTechnique {
    
    private Long id;
    private Long vehiculeId;
    private String libelle;
    private LocalDate dateEntretien;
    private String compteRendu;
    private Integer kilometrageEffectue;
    private Double cout;
    private String prestataire;
    private String piecesChangees;
    
    // Constructeurs
    public EntretienTechnique() {}
    
    public EntretienTechnique(Long vehiculeId, String libelle, LocalDate dateEntretien) {
        this.vehiculeId = vehiculeId;
        this.libelle = libelle;
        this.dateEntretien = dateEntretien;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }
    
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    
    public LocalDate getDateEntretien() { return dateEntretien; }
    public void setDateEntretien(LocalDate dateEntretien) { this.dateEntretien = dateEntretien; }
    
    public String getCompteRendu() { return compteRendu; }
    public void setCompteRendu(String compteRendu) { this.compteRendu = compteRendu; }
    
    public Integer getKilometrageEffectue() { return kilometrageEffectue; }
    public void setKilometrageEffectue(Integer kilometrageEffectue) { this.kilometrageEffectue = kilometrageEffectue; }
    
    public Double getCout() { return cout; }
    public void setCout(Double cout) { this.cout = cout; }
    
    public String getPrestataire() { return prestataire; }
    public void setPrestataire(String prestataire) { this.prestataire = prestataire; }
    
    public String getPiecesChangees() { return piecesChangees; }
    public void setPiecesChangees(String piecesChangees) { this.piecesChangees = piecesChangees; }
    
    @Override
    public String toString() {
        return "EntretienTechnique [id=" + id + ", vehiculeId=" + vehiculeId + 
               ", libelle=" + libelle + ", dateEntretien=" + dateEntretien + 
               ", cout=" + cout + ", prestataire=" + prestataire + "]";
    }
}
