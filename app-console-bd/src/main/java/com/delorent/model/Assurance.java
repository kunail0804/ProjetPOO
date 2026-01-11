package com.delorent.model;

public class Assurance 
  {
   private Integer idAssurance;
   private String nom;
   private double tarifJournalier;

   public Assurance (Integer idAssurance, String nom, double tarifJournalier){
       this.idAssurance = idAssurance;
       this.nom = nom;
       this.tarifJournalier = tarifJournalier;
    }

    public Assurance (String nom, double tarifJournalier){
        this.nom = nom;
        this.tarifJournalier = tarifJournalier; 
    }

   public Integer getIdAssurance () { return idAssurance; }
   public String getNom () { return nom; }
   public double getTarifJournalier() { return tarifJournalier; }


      public void setIdAssurance(Integer idAssurance) 
      {
        this.idAssurance = idAssurance;
    }
	  public void setNom(String nom) 
      {
        this.nom = nom;
    }

    public void setTarifJournalier(double tarifJournalier) 
    {
        this.tarifJournalier = tarifJournalier;
    }
   
}


