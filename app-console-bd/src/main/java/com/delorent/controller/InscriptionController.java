package com.delorent.controller;

import com.delorent.repository.EntrepriseRepository;
import com.delorent.repository.UserRepository;

public class InscriptionController {
        private final UserRepository utilisateurRepo;
        private final EntrepriseRepository entrepriseRepo;
        private final int utilisateurEnCoursId;

        public InscriptionController(UserRepository utilisateurRepo, EntrepriseRepository entrepriseRepo){
            this.utilisateurRepo = utilisateurRepo;
            this.entrepriseRepo = entrepriseRepo;
            this.utilisateurEnCoursId = -1;
        }

        public void inscrire(String email, String motDePasse, String telephone) {
                
        }

        public void inscrireEntreprise(String raisonSociale, String siret){
            
        }
}
