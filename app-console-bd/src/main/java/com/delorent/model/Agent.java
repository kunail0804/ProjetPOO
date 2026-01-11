package com.delorent.model;

public class Agent extends Utilisateur{

   private String nom;
   private String prenom;

   public Agent(int idUtilisateur, String adresse, String ville, String codePostal, String region, String telephone,
      String mail, String motDePasse, String typeAgent, String nom, String prenom) {
    super(idUtilisateur, adresse, ville, codePostal, region, telephone, mail, motDePasse);
    this.nom = nom;
    this.prenom = prenom;
  }

  public Agent() {
    super();
  }

  public String getNomComplet() {
    return this.prenom + " " + this.nom;
}
  
   public String getNom () { return nom; }
   public String getPrenom () { return prenom; }
	 
      public void setNom(String nom) 
      
      {
        this.nom = nom;
    }

	  public void setPrenom(String prenom) 
      
      {
        this.prenom = prenom;
    }
}
