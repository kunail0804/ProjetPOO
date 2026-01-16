package com.delorent.model.Louable;

public abstract class Louable {

    private int idLouable;
    private int idAgent;
    private double prixJour;
    private StatutLouable statut;
    private String lieuPrincipal;

    public Louable(int idLouable, int idAgent, double prixJour, String lieuPrincipal) {
        this.idLouable = idLouable;
        this.idAgent = idAgent;
        this.prixJour = prixJour;
        this.statut = StatutLouable.INDISPONIBLE;
        this.lieuPrincipal = lieuPrincipal;
    }

    public Louable(int idAgent, double prixJour, String lieuPrincipal) {
        this.idAgent = idAgent;
        this.prixJour = prixJour;
        this.statut = StatutLouable.INDISPONIBLE;
        this.lieuPrincipal = lieuPrincipal;
    }

    public Louable(int idLouable, int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal) {
        this.idLouable = idLouable;
        this.idAgent = idAgent;
        this.prixJour = prixJour;
        this.statut = statut;
        this.lieuPrincipal = lieuPrincipal;
    }

    public Louable(int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal) {
        this.idAgent = idAgent;
        this.prixJour = prixJour;
        this.statut = statut;
        this.lieuPrincipal = lieuPrincipal;
    }


    public int getIdLouable() {
        return idLouable;
    }

    public int getIdAgent() {
        return idAgent;
    }

    public double getPrixJour() {
        return prixJour;
    }

    public StatutLouable getStatut() {
        return statut;
    }

    public String getLieuPrincipal() {
        return lieuPrincipal;
    }

    public void setIdLouable(int idLouable) {
        this.idLouable = idLouable;
    }

    public void setIdAgent(int idAgent) {
        this.idAgent = idAgent;
    }

    public void setPrixJour(double prixJour) {
        this.prixJour = prixJour;
    }

    public void setStatut(StatutLouable statut) {
        this.statut = statut;
    }

    public void setLieuPrincipal(String lieuPrincipal) {
        this.lieuPrincipal = lieuPrincipal;
    }

    

}