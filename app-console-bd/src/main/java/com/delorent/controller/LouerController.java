package com.delorent.controller;

import com.delorent.dto.DemandeLocation;
import com.delorent.service.ServiceLocation;

import java.time.LocalDate;

public class LouerController {

    private final ServiceLocation serviceLocation;

    public LouerController(ServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public ContratLocation louerVehicule(int idLoueur,
                                         int idLouable,
                                         int idAssurance,
                                         LocalDate dateDebut,
                                         LocalDate dateFin,
                                         String lieuDepotOptionnel) {

        DemandeLocation demande = new DemandeLocation(
                idLoueur,
                idLouable,
                idAssurance,
                dateDebut,
                dateFin,
                lieuDepotOptionnel
        );

        return serviceLocation.louer(demande);
    }
}