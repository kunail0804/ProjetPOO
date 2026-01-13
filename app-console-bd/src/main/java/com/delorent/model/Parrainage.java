package com.delorent.model;

import java.time.LocalDate;

public class Parrainage {
    private Integer idParrainage;
    private int idParrain;
    private int idFilleul;
    private LocalDate dateCreation;
    private String statut; // EN_ATTENTE, VALIDE
    private LocalDate dateValidation;

    public Integer getIdParrainage() { return idParrainage; }
    public void setIdParrainage(Integer idParrainage) { this.idParrainage = idParrainage; }

    public int getIdParrain() { return idParrain; }
    public void setIdParrain(int idParrain) { this.idParrain = idParrain; }

    public int getIdFilleul() { return idFilleul; }
    public void setIdFilleul(int idFilleul) { this.idFilleul = idFilleul; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDate getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDate dateValidation) { this.dateValidation = dateValidation; }
}
