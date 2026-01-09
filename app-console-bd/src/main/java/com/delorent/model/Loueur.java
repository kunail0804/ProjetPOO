package com.delorent.model;

public class Loueur extends Utilisateur {

    private String nom;
    private String prenom;

    // Constructeur
    public Loueur(int idUtilisateur, String mdp, String ville,
                  String codePostal, String region,
                  String telephone, String mail,
                  String dateInscription,
                  String nom, String prenom) {

        // Appel du constructeur de la classe mère
        super(idUtilisateur, mdp, ville, codePostal, region,
              telephone, mail, dateInscription);

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

