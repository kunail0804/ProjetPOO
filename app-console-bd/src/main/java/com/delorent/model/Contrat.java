package com.delorent.model;

import java.sql.Date;

public class Contrat {

    private Integer idContrat;
    private Date dateDebut;
    private Date dateFin;
    private Double prixFinal;
    private String etat;
    private String lieuPrise;
    private String lieuDepot;

    public Contrat() {}

    public Contrat(Integer idContrat,
                   Date dateDebut,
                   Date dateFin,
                   Double prixFinal,
                   String etat,
                   String lieuPrise,
                   String lieuDepot) {
        this.idContrat = idContrat;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prixFinal = prixFinal;
        this.etat = etat;
        this.lieuPrise = lieuPrise;
        this.lieuDepot = lieuDepot;
    }

    public Integer getIdContrat() { return idContrat; }
    public void setIdContrat(Integer idContrat) { this.idContrat = idContrat; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }

    public Double getPrixFinal() { return prixFinal; }
    public void setPrixFinal(Double prixFinal) { this.prixFinal = prixFinal; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public String getLieuPrise() { return lieuPrise; }
    public void setLieuPrise(String lieuPrise) { this.lieuPrise = lieuPrise; }

    public String getLieuDepot() { return lieuDepot; }
    public void setLieuDepot(String lieuDepot) { this.lieuDepot = lieuDepot; }

    @Override
    public String toString() {
        return "Contrat{" +
                "idContrat=" + idContrat +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", prixFinal=" + prixFinal +
                ", etat='" + etat + '\'' +
                ", lieuPrise='" + lieuPrise + '\'' +
                ", lieuDepot='" + lieuDepot + '\'' +
                '}';
    }
}