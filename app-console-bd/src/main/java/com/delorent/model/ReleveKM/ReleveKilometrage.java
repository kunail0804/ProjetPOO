package com.delorent.model.ReleveKM;

import java.time.LocalDateTime;

public class ReleveKilometrage {

    private int idReleve;
    private int idContrat;
    private ReleveType typeReleve;
    private int kilometrage;
    private String photoPath;
    private LocalDateTime dateSaisie;

    public ReleveKilometrage(int idReleve, int idContrat, ReleveType typeReleve, int kilometrage, String photoPath, LocalDateTime dateSaisie) {
        this.idReleve = idReleve;
        this.idContrat = idContrat;
        this.typeReleve = typeReleve;
        this.kilometrage = kilometrage;
        this.photoPath = photoPath;
        this.dateSaisie = dateSaisie;
    }

    public ReleveKilometrage(){
        super();
    }

    public int getIdReleve() { return idReleve; }
    public int getIdContrat() { return idContrat; }
    public ReleveType getTypeReleve() { return typeReleve; }
    public int getKilometrage() { return kilometrage; }
    public String getPhotoPath() { return photoPath; }
    public LocalDateTime getDateSaisie() { return dateSaisie; }

    public void setIdReleve(int idReleve) { this.idReleve = idReleve; }
    public void setIdContrat(int idContrat) { this.idContrat = idContrat; }
    public void setTypeReleve(ReleveType typeReleve) { this.typeReleve = typeReleve; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setDateSaisie(LocalDateTime dateSaisie) { this.dateSaisie = dateSaisie; }
}