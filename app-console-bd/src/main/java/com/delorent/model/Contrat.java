package com.delorent.model;

import java.sql.Date;

public class Contrat {
   private Date dateDebut;
   private Date dateFin;
   private String lieuPrise;
   private String lieuDepot;

   public Contrat (Date dateDebut, Date dateFin, String lieuPrise, String lieuDepot)
       {
       this.dateDebut = dateDebut;
       this.dateFin = dateFin;
       this.lieuPrise = lieuPrise;
	   this.lieuDepot = lieuDepot;
       }

   public Date getDateDebut () { return dateDebut; }
   public Date getDateFin () { return dateFin; }
   public String getLieuPrise () { return lieuPrise; }
   public String getLieuDepot () { return lieuDepot; }


      public void setDateDebut(Date DateDebut) 
      
        {
        this.dateDebut = dateDebut;  
    }
	  public void setDateFin(Date DateFin) 
        {
        this.dateFin = dateFin; 
    }
	  public void setLieuPrise(String lieuPrise) 
        {
        this.lieuPrise = lieuPrise;
    }
	  public void setLieuDepot(String lieuDepot) 
      
        {this.lieuDepot = lieuDepot;

    }

}
