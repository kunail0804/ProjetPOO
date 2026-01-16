package com.delorent.model.Utilisateur;



public abstract class Utilisateur {

   private int idUtilisateur;
   private String adresse;
    private String ville;
    private String codePostal;
    private String region;
    private String telephone;
    private String mail;
    private String motDePasse;

    public Utilisateur(String mail, String motDePasse, String adresse,
                       String ville, String codePostal, String region,
                       String telephone
                       ) {
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.region = region;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
    }

    public Utilisateur(int idUtilisateur, String mail, String motDePasse, String adresse,
                       String ville, String codePostal, String region,
                       String telephone
                       ) {
        this.idUtilisateur = idUtilisateur;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
        this.region = region;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
    }

    public Utilisateur() {
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
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

}
