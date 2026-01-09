package com.delorent.controller;

import com.delorent.repository.OptionPayanteRepository;
import com.delorent.repository.UserRepository;

public class Inscription {
        private final UserRepository utilisateurRepo;
        private final OptionPayanteRepository entrepriseRepo;
        private final int utilisateurEnCoursId;

        public Inscription(UserRepository utilisateurRepo, OptionPayanteRepository entrepriseRepo){
            this.utilisateurRepo = utilisateurRepo;
            this.entrepriseRepo = entrepriseRepo;
            this.utilisateurEnCoursId = -1;
        }

        public void inscrire(String email, String motDePasse, String telephone) {
                
        }

        public void inscrireEntreprise(String raisonSociale, String siret){
            
        }
}
