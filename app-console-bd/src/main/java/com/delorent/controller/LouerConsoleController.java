package com.delorent.controller;

import com.delorent.model.Contrat;
import com.delorent.service.ServiceLocation;

import java.time.LocalDate;

public class LouerConsoleController {

    private final ServiceLocation serviceLocation;

    public LouerConsoleController(ServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public Contrat louerVehicule(int idLoueur,
                                 int idLouable,
                                 int idAssurance,
                                 LocalDate dateDebut,
                                 LocalDate dateFin,
                                 String lieuDepotOptionnel) {

        return serviceLocation.louer(idLoueur, idLouable, idAssurance, dateDebut, dateFin, lieuDepotOptionnel);
    }
}