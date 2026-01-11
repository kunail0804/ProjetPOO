package com.delorent.model.Utilisateur;

public class Loueur extends Utilisateur {

    private String nom;
    private String prenom;

    // Constructeur
    public Loueur(String mail, String motDePasse, String adresse,
                       String ville, String codePostal, String region,
                       String telephone,String nom, String prenom) {

        // Appel du constructeur de la classe mère
        super(mail, motDePasse, adresse, ville, codePostal, region, telephone);
        this.nom = nom;
        this.prenom = prenom;
    }

    public Loueur(int idUtilisateur, String mail, String motDePasse, String adresse,
                       String ville, String codePostal, String region,
                       String telephone,String nom, String prenom) {

        // Appel du constructeur de la classe mère
        super(idUtilisateur, mail, motDePasse, adresse, ville, codePostal, region, telephone);
        this.nom = nom;
        this.prenom = prenom;
    }

    public Loueur() {
        super(); // Appelle le constructeur vide de Utilisateur
    }

    // Méthode utilitaire pour l'affichage (Thymeleaf appelle getNomComplet() quand on écrit ${loueur.nomComplet})
    public String getNomComplet() {
        return this.getPrenom() + " " + this.getNom();
    }
    // Getters
    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}

