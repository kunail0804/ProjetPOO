package com.delorent.model;

public class EntrepriseEntretien extends Utilisateur {

    private String nomEntreprise;
    private String raisonSoc;
    private String noSiret;

    public EntrepriseEntretien(
            String adresse,
            String ville,
            String codePostal,
            String region,
            String telephone,
            String mail,
            String motDePasse,
            String nomEntreprise,
            String raisonSoc,
            String noSiret
    ) {
        super(adresse, ville, codePostal, region, telephone, mail, motDePasse);
        this.nomEntreprise = nomEntreprise;
        this.raisonSoc = raisonSoc;
        this.noSiret = noSiret;
    }

    public EntrepriseEntretien(
            int idUtilisateur,
            String adresse,
            String ville,
            String codePostal,
            String region,
            String telephone,
            String mail,
            String motDePasse,
            String nomEntreprise,
            String raisonSoc,
            String noSiret
    ) {
        super(idUtilisateur, adresse, ville, codePostal, region, telephone, mail, motDePasse);
        this.nomEntreprise = nomEntreprise;
        this.raisonSoc = raisonSoc;
        this.noSiret = noSiret;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getRaisonSoc() {
        return raisonSoc;
    }

    public void setRaisonSoc(String raisonSoc) {
        this.raisonSoc = raisonSoc;
    }

    public String getNoSiret() {
        return noSiret;
    }

    public void setNoSiret(String noSiret) {
        this.noSiret = noSiret;
    }
}
