package com.delorent.model;

public class Assurance 
  {
   private Integer identifiant;
   private String nom;

   public Assurance (Integer identifiant, String nom)
       {
       this.identifiant = identifiant;
       this.nom = nom;
       }

   public Integer getIdentifiant () { return identifiant; }
   public String getNom () { return nom; }


      public void setIdentifiant(Integer identifiant) 
      {
        this.identifiant = identifiant;
    }
	  public void setNom(String nom) 
      {
        this.nom = nom;
    }
   
}


