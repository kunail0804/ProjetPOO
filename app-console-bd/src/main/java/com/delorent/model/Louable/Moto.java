package com.delorent.model.Louable;

public class Moto extends Vehicule {

    private int cylindreeCc;
    private int puissanceCh;
    private TypeMoto typeMoto;
    private String permisRequis;

    public Moto(int idLouable, int idAgent, double prixJour, String lieuPrincipal, String marque, String modele, int annee,
            String couleur, String immatriculation, int kilometrage, int cylindreeCc, int puissanceCh,
            TypeMoto typeMoto, String permisRequis) {
        super(idLouable, idAgent, prixJour, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.cylindreeCc = cylindreeCc;
        this.puissanceCh = puissanceCh;
        this.typeMoto = typeMoto;
        this.permisRequis = permisRequis;
    }
    public Moto(int idAgent,double prixJour, String lieuPrincipal, String marque, String modele, int annee, String couleur,
            String immatriculation, int kilometrage, int cylindreeCc, int puissanceCh, TypeMoto typeMoto,
            String permisRequis) {
        super(idAgent, prixJour, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.cylindreeCc = cylindreeCc;
        this.puissanceCh = puissanceCh;
        this.typeMoto = typeMoto;
        this.permisRequis = permisRequis;
    }
    public Moto(int idLouable, int idAgent, double prixJour, StatutLouable statut, String lieuPrincipal, String marque,
            String modele, int annee, String couleur, String immatriculation, int kilometrage, int cylindreeCc,
            int puissanceCh, TypeMoto typeMoto, String permisRequis) {
        super(idLouable, idAgent, prixJour, statut, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
        this.cylindreeCc = cylindreeCc;
        this.puissanceCh = puissanceCh;
        this.typeMoto = typeMoto;
        this.permisRequis = permisRequis;
    }
    public Moto(int idAgent,double prixJour, StatutLouable statut, String lieuPrincipal, String marque, String modele, int annee,
            String couleur, String immatriculation, int kilometrage, int cylindreeCc, int puissanceCh,
            TypeMoto typeMoto, String permisRequis) {
        super(idAgent, prixJour, statut, lieuPrincipal, marque, modele, annee, couleur, immatriculation, kilometrage);
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