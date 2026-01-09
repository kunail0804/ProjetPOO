package com.delorent.model;

public class OptionPayante {
    // Champs exacts de la base de données (camelCase)
    private int idOption;        
    private String nomOption;    
    private float prixMensuel; 
    private String description;  
    
    // --- INDISPENSABLES POUR LE HTML (Viennent de la table SOUSCRIT) ---
    private boolean estActive;   
    private String dateSouscription;

    public OptionPayante() {}

    // Getters / Setters
    public int getIdOption() { return idOption; }
    public void setIdOption(int idOption) { this.idOption = idOption; }

    public String getNomOption() { return nomOption; }
    public void setNomOption(String nomOption) { this.nomOption = nomOption; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrixMensuel() { return prixMensuel; }
    public void setPrixMensuel(float prixMensuel) { this.prixMensuel = prixMensuel; }

    // Getters / Setters pour les champs techniques
    public boolean isEstActive() { return estActive; }
    public void setEstActive(boolean estActive) { this.estActive = estActive; }

    public String getDateSouscription() { return dateSouscription; }
    public void setDateSouscription(String dateSouscription) { this.dateSouscription = dateSouscription; }
}