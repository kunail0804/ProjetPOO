package com.delorent.model.Utilisateur;

public class AgentProfessionnel extends Agent {

    private String siret;

    public AgentProfessionnel(int idUtilisateur, String mail, String motDePasse, String adresse,
                              String ville, String codePostal, String region, String telephone, 
                              String nom, String prenom, String siret) {
        super(idUtilisateur, mail, motDePasse, adresse, ville, codePostal, region, telephone, nom, prenom);
        this.siret = siret;
    }
    
    public AgentProfessionnel(String mail, String motDePasse, String adresse,
                              String ville, String codePostal, String region, String telephone, 
                              String nom, String prenom, String siret) {
        super(mail, motDePasse, adresse, ville, codePostal, region, telephone, nom, prenom);
        this.siret = siret;
    }

    @Override
    public boolean isProfessionnel() {
        return true;
    }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }
}