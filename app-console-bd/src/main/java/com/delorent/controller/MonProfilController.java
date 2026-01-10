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

    public void getProfil(){
        return;
    }

    public void modifierMail(String mail){
        return;
    }

    public void midifierTel(int telephone){
        return;
    }

    public void modifierMotDePasse(String ancien, String nouveau){
        return;
    }

    public void ajouterAssurance(Assurance assurance){
        return;
    }

    public void supprimerAssurance(Assurance assurance){
        return;
    }

    public void getOptions(){
        return;
    }
}
