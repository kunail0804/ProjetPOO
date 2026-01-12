package com.delorent.model.Louable;

public class Camion extends Vehicule {

    private int chargeMaxKg;
    private double volumeUtileM3;
    private double hauteurM;
    private double longueurM;
    private String permisRequis;

    public Camion(int idLouable, int idAgent, double prixJour, String lieuPrincipal, String marque, String modele, int annee,
            String couleur, String immatriculation, int kilometrage, int chargeMaxKg, double volumeUtileM3,
            double hauteurM, double longueurM, String permisRequis) {
        super(idLouable, idAgent, prixJour, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.chargeMaxKg = chargeMaxKg;
        this.volumeUtileM3 = volumeUtileM3;
        this.hauteurM = hauteurM;
        this.longueurM = longueurM;
        this.permisRequis = permisRequis;
    }

    public Camion( int idAgent,double prixJour, String lieuPrincipal, String marque, String modele, int annee, String couleur,
            String immatriculation, int kilometrage, int chargeMaxKg, double volumeUtileM3, double hauteurM,
            double longueurM, String permisRequis) {
        super(idAgent, prixJour, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.chargeMaxKg = chargeMaxKg;
        this.volumeUtileM3 = volumeUtileM3;
        this.hauteurM = hauteurM;
        this.longueurM = longueurM;
        this.permisRequis = permisRequis;
    }

    public Camion(int idLouable, int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal, String marque,
            String modele, int annee, String couleur, String immatriculation, int kilometrage, int chargeMaxKg,
            double volumeUtileM3, double hauteurM, double longueurM, String permisRequis) {
        super(idLouable, idAgent, prixJour, statut, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.chargeMaxKg = chargeMaxKg;
        this.volumeUtileM3 = volumeUtileM3;
        this.hauteurM = hauteurM;
        this.longueurM = longueurM;
        this.permisRequis = permisRequis;
    }

    public Camion( int idAgent,double prixJour, StatutLouable statut, String lieuPrincipal, String marque, String modele, int annee,
            String couleur, String immatriculation, int kilometrage, int chargeMaxKg, double volumeUtileM3,
            double hauteurM, double longueurM, String permisRequis) {
        super(idAgent, prixJour, statut, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
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