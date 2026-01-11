package com.delorent.model;

import java.time.LocalDate;

public class Disponibilite {

    private Integer idDisponibilite;
    private Integer idLouable;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Disponibilite(Integer idDisponibilite, Integer idLouable, LocalDate dateDebut, LocalDate dateFin) {
        this.idDisponibilite = idDisponibilite;
        this.idLouable = idLouable;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Disponibilite(Integer idLouable, LocalDate dateDebut, LocalDate dateFin) {
        this.idLouable = idLouable;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public Integer getIdDisponibilite() { return idDisponibilite; }
    public Integer getIdLouable() { return idLouable; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }

    public void setIdDisponibilite(Integer idDisponibilite) { this.idDisponibilite = idDisponibilite; }
    public void setIdLouable(Integer idLouable) { this.idLouable = idLouable; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    // Bonus utile côté validations
    public boolean couvre(LocalDate debut, LocalDate fin) {
        return (dateDebut == null || dateFin == null || debut == null || fin == null)
                ? false
                : (!dateDebut.isAfter(debut) && !dateFin.isBefore(fin));
    }
}
