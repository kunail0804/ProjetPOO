package com.delorent.service;

import com.delorent.model.Contrat;

import java.time.LocalDate;

public interface ServiceLocation {

    Contrat louer(int idLoueur,
                  int idLouable,
                  int idAssurance,
                  LocalDate dateDebut,
                  LocalDate dateFin,
                  String lieuDepotOptionnel);
}