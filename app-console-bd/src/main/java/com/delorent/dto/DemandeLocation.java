package com.delorent.dto;


import java.time.LocalDate;

public class DemandeLocation {

    public final int idLoueur;
    public final int idLouable;
    public final int idAssurance;
    public final LocalDate dateDebut;
    public final LocalDate dateFin;
    public final String lieuDepot; // null si pas demandé / pas autorisé

    public DemandeLocation(int idLoueur,
                           int idLouable,
                           int idAssurance,
                           LocalDate dateDebut,
                           LocalDate dateFin,
                           String lieuDepot) {
        this.idLoueur = idLoueur;
        this.idLouable = idLouable;
        this.idAssurance = idAssurance;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieuDepot = lieuDepot;
    }
}