package com.delorent.service;

import com.delorent.model.EntrepriseEntretien;
import com.delorent.repository.EntrepriseRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthEntrepriseService {

    private final EntrepriseRepository entrepriseRepo;

    public AuthEntrepriseService(EntrepriseRepository entrepriseRepo) {
        this.entrepriseRepo = entrepriseRepo;
    }

    public EntrepriseEntretien login(String email, String motDePasse) {
        if (email == null || email.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            return null;
        }
        return entrepriseRepo.findEntrepriseByCredentials(email.trim(), motDePasse);
    }
}
