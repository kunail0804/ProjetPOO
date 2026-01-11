package com.delorent.controller;

public class ProfilEditionForm {
    // Commun
    private String mail;
    private String motDePasse;
    private String adresse;
    private String ville;
    private String codePostal;
    private String region;
    private String telephone;

    // Spécifique Agent/Loueur
    private String nom;
    private String prenom;

    // Spécifique EntrepriseEntretien
    private String nomEntreprise;
    private String raisonSoc;
    private String noSiret;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
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
