// FICHIER: src/main/java/com/delorent/model/Louable/Disponibilite.java
package com.delorent.model.Louable;

import java.time.LocalDate;

public class Disponibilite {

    private Integer idDisponibilite;
    private Integer idLouable;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // DB fields (présents dans ta table)
    private boolean estReservee;
    private Double prixJournalier;

    public Disponibilite(Integer idDisponibilite, Integer idLouable, LocalDate dateDebut, LocalDate dateFin,
                         boolean estReservee, Double prixJournalier) {
        this.idDisponibilite = idDisponibilite;
        this.idLouable = idLouable;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.estReservee = estReservee;
        this.prixJournalier = prixJournalier;
    }

    public Disponibilite(Integer idLouable, LocalDate dateDebut, LocalDate dateFin) {
        this(null, idLouable, dateDebut, dateFin, false, null);
    }

    public Disponibilite(Integer idLouable, LocalDate dateDebut, LocalDate dateFin, boolean estReservee, Double prixJournalier) {
        this(null, idLouable, dateDebut, dateFin, estReservee, prixJournalier);
    }

    public Integer getIdDisponibilite() { return idDisponibilite; }
    public Integer getIdLouable() { return idLouable; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public boolean isEstReservee() { return estReservee; }
    public Double getPrixJournalier() { return prixJournalier; }

    public void setIdDisponibilite(Integer idDisponibilite) { this.idDisponibilite = idDisponibilite; }
    public void setIdLouable(Integer idLouable) { this.idLouable = idLouable; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public void setEstReservee(boolean estReservee) { this.estReservee = estReservee; }
    public void setPrixJournalier(Double prixJournalier) { this.prixJournalier = prixJournalier; }

    // couvre un intervalle [debut..fin] inclusif
    public boolean couvre(LocalDate debut, LocalDate fin) {
        return (dateDebut == null || dateFin == null || debut == null || fin == null)
                ? false
                : (!dateDebut.isAfter(debut) && !dateFin.isBefore(fin));
    }
}