package com.delorent.model;

public class OptionPayante {

    private int idOption;
    private String nomOption;
    private float prixMensuel;
    private String description;

    // Pour affichage agent
    private boolean activePourAgent;

    public int getIdOption() { return idOption; }
    public void setIdOption(int idOption) { this.idOption = idOption; }

    public String getNomOption() { return nomOption; }
    public void setNomOption(String nomOption) { this.nomOption = nomOption; }

    public float getPrixMensuel() { return prixMensuel; }
    public void setPrixMensuel(float prixMensuel) { this.prixMensuel = prixMensuel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActivePourAgent() { return activePourAgent; }
    public void setActivePourAgent(boolean activePourAgent) { this.activePourAgent = activePourAgent; }
}
