package com.delorent.controller;
public class Inscription {
        private final UtilisateurRepository utilisateurRepo;
        private final EntrepriseRepository entrepriseRepo;
        private final int utilisateurEnCoursId;
        public void inscrire(String email, String motDePasse, String telephone,
                TypeUtilisateur typeUtilisateur) {
                utilisateurEnCoursId = utilisateurRepo.insert(email, motDePasse, telephone, typeUtilisateur);
        }

        public void inscrireEntreprise(String raisonSociale, String siret){
            entrepriseRepo.insert(raisonSociale, siret, utilisateurEnCoursId);
        }
}
