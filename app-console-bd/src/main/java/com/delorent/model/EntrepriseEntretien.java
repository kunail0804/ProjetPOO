package com.delorent.model;

public class EntrepriseEntretien extends Utilisateur {

    private String nomEntreprise;
    private String raisonSoc;
    private String noSiret;

    public EntrepriseEntretien(
            String mail, 
            String motDePasse, 
            String adresse,
            String ville, 
            String codePostal, 
            String region,
            String telephone,
            String nomEntreprise,
            String raisonSoc,
            String noSiret
    ) {
        super(mail, motDePasse, adresse, ville, codePostal, region, telephone);

        this.nomEntreprise = nomEntreprise;
        this.raisonSoc = raisonSoc;
        this.noSiret = noSiret;
    }

    public EntrepriseEntretien(
            int idUtilisateur,
            String mail, 
            String motDePasse, 
            String adresse,
            String ville, 
            String codePostal, 
            String region,
            String telephone,
            String nomEntreprise,
            String raisonSoc,
            String noSiret
    ) {
        super(idUtilisateur, mail, motDePasse, adresse, ville, codePostal, region, telephone);

        this.nomEntreprise = nomEntreprise;
        this.raisonSoc = raisonSoc;
        this.noSiret = noSiret;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public String getRaisonSoc() {
        return raisonSoc;
    }

    public String getNoSiret() {
        return noSiret;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public void setRaisonSoc(String raisonSoc) {
        this.raisonSoc = raisonSoc;
    }

    public void setNoSiret(String noSiret) {
        this.noSiret = noSiret;
    }
}
