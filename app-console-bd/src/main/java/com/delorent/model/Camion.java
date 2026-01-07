package com.delorent.model;

public class Camion extends Vehicule {

    private int chargeMaxKg;
    private double volumeUtileM3;
    private double hauteurM;
    private double longueurM;
    private String permisRequis; // Exemple: "B", "C"
    
    public Camion(int id, String marque, String modele, double prixJour, String immatriculation, int chargeMaxKg,
            double volumeUtileM3, double hauteurM, double longueurM, String permisRequis) {
        super(id, marque, modele, prixJour, immatriculation);
        this.chargeMaxKg = chargeMaxKg;
        this.volumeUtileM3 = volumeUtileM3;
        this.hauteurM = hauteurM;
        this.longueurM = longueurM;
        this.permisRequis = permisRequis;
    }

    public int getChargeMaxKg() {
        return chargeMaxKg;
    }

    public double getVolumeUtileM3() {
        return volumeUtileM3;
    }

    public double getHauteurM() {
        return hauteurM;
    }

    public double getLongueurM() {
        return longueurM;
    }

    public String getPermisRequis() {
        return permisRequis;
    }

    public void setChargeMaxKg(int chargeMaxKg) {
        this.chargeMaxKg = chargeMaxKg;
    }

    public void setVolumeUtileM3(double volumeUtileM3) {
        this.volumeUtileM3 = volumeUtileM3;
    }

    public void setHauteurM(double hauteurM) {
        this.hauteurM = hauteurM;
    }

    public void setLongueurM(double longueurM) {
        this.longueurM = longueurM;
    }

    public void setPermisRequis(String permisRequis) {
        this.permisRequis = permisRequis;
    }


}