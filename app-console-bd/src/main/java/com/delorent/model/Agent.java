package com.delorent.model;

public class Agent {

   private String typeAgent;
   private String nom;
   private String prenom;

   public Agent (String typeAgent, String nom, String prenom)
       {
       this.typeAgent = typeAgent;
       this.nom = nom;
       this.prenom = prenom;
       }

   public String getTypeAgent () { return typeAgent; }
   public String getNom () { return nom; }
   public String getPrenom () { return prenom; }


   public void setTypeAgent(String typeAgent) 
      {
        this.typeAgent = typeAgent;
      }
	 
      public void setNom(String nom) 
      
      {
        this.nom = nom;
    }

	  public void setPrenom(String prenom) 
      
      {
        this.prenom = prenom;
    }
}
