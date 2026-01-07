package com.delorent.model;

public class Moto extends Vehicule {

    private int cylindreeCc;
    private int puissanceCh;
    private TypeMoto typeMoto;
    private String permisRequis;

    public Moto(int id, String marque, String modele, double prixJour, String immatriculation, int cylindreeCc,
            int puissanceCh, TypeMoto typeMoto, String permisRequis) {
        super(id, marque, modele, prixJour, immatriculation);
        this.cylindreeCc = cylindreeCc;
        this.puissanceCh = puissanceCh;
        this.typeMoto = typeMoto;
        this.permisRequis = permisRequis;
    }
    public int getCylindreeCc() {
        return cylindreeCc;
    }
    public int getPuissanceCh() {
        return puissanceCh;
    }
    public TypeMoto getTypeMoto() {
        return typeMoto;
    }
    public String getPermisRequis() {
        return permisRequis;
    }

    public void setCylindreeCc(int cylindreeCc) {
        this.cylindreeCc = cylindreeCc;
    }

    public void setPuissanceCh(int puissanceCh) {
        this.puissanceCh = puissanceCh;
    }

    public void setTypeMoto(TypeMoto typeMoto) {
        this.typeMoto = typeMoto;
    }

    public void setPermisRequis(String permisRequis) {
        this.permisRequis = permisRequis;
    }

}