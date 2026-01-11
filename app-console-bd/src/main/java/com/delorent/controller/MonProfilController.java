package com.delorent.controller;

import java.util.List;
import com.delorent.model.Assurance;
import com.delorent.model.EntrepriseEntretien;

public class MonProfilController {
    private final List<Assurance> assurances;
    private final boolean contratManuel;
    private final EntrepriseEntretien entrepriseEntretient;

    public MonProfilController(List<Assurance> assurances, boolean contratManuel, EntrepriseEntretien entrepriseEntretient){
        this.assurances = assurances;
        this.contratManuel = contratManuel;
        this.entrepriseEntretient = entrepriseEntretient;
    }

    
}
