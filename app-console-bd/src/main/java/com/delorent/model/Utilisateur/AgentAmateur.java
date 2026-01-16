package com.delorent.model.Utilisateur;

public class AgentAmateur extends Agent {

    public AgentAmateur(int idUtilisateur, String mail, String motDePasse, String adresse,
                        String ville, String codePostal, String region, String telephone, 
                        String nom, String prenom) {
        super(idUtilisateur, mail, motDePasse, adresse, ville, codePostal, region, telephone, nom, prenom);
    }
    
    public AgentAmateur(String mail, String motDePasse, String adresse,
                        String ville, String codePostal, String region, String telephone, 
                        String nom, String prenom) {
        super(mail, motDePasse, adresse, ville, codePostal, region, telephone, nom, prenom);
    }

    @Override
    public boolean isProfessionnel() {
        return false;
    }
}