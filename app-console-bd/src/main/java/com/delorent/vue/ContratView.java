package com.delorent.vue;


import java.time.LocalDate;

public class ContratView {
    private final int id;
    private final LocalDate dateDebut;
    private final LocalDate dateFin;
    private final String lieuPrise;
    private final String lieuDepot;
    private final String assuranceNom;

    public ContratView(int id, LocalDate dateDebut, LocalDate dateFin,
                       String lieuPrise, String lieuDepot, String assuranceNom) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuPrise = lieuPrise;
        this.lieuDepot = lieuDepot;
        this.assuranceNom = assuranceNom;
    }

    public int getId() { return id; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public String getLieuPrise() { return lieuPrise; }
    public String getLieuDepot() { return lieuDepot; }
    public String getAssuranceNom() { return assuranceNom; }
}