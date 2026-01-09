package com.delorent.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delorent.repository.AgentRepository;
import com.delorent.repository.EntrepriseEntretienRepository;
import com.delorent.repository.LoueurRepository;

import com.delorent.model.Agent;
import com.delorent.model.EntrepriseEntretien;
import com.delorent.model.Loueur;

@Service
public class UtilisateurService {

    private AgentRepository agentRepository;
    private LoueurRepository loueurRepository;
    private EntrepriseEntretienRepository entrepriseEntretienRepository;

    public UtilisateurService(AgentRepository agentRepository, LoueurRepository loueurRepository, EntrepriseEntretienRepository entrepriseEntretienRepository) {
        this.agentRepository = agentRepository;
        this.loueurRepository = loueurRepository;
        this.entrepriseEntretienRepository = entrepriseEntretienRepository;
    }

    // --- Helpers validation/normalisation ---

    private String clean(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void require(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Champ obligatoire manquant: " + fieldName);
        }
    }

    private String hashPassword(String rawPassword) {
        // TODO
        return rawPassword;
    }

    // --- Méthodes demandées ---

    @Transactional
    public long ajouterAgent(String username, String email, String password) {
        String u = clean(username);
        String e = clean(email);
        String p = clean(password);

        require(u, "username");
        require(e, "email");
        require(p, "password");

        String passwordHash = hashPassword(p);
        return agentRepository.add(new Agent(e, passwordHash, "", "", "", "", "", u, ""));
    }

    @Transactional
    public long ajouterLoueur(String username, String email, String password) {
        String u = clean(username);
        String e = clean(email);
        String p = clean(password);

        require(u, "username");
        require(e, "email");
        require(p, "password");

        String passwordHash = hashPassword(p);
        return loueurRepository.add(new Loueur(e, passwordHash, "", "", "", "", "", u, ""));
    }

    @Transactional
    public long ajouterEntrepriseEntretien(String username, String email, String password,
                                          String raisonSociale, String siret) {
        String u = clean(username);
        String e = clean(email);
        String p = clean(password);
        String rs = clean(raisonSociale);
        String si = clean(siret);

        require(u, "username");
        require(e, "email");
        require(p, "password");
        require(rs, "raisonSociale");
        require(si, "siret");

        String passwordHash = hashPassword(p);

        return entrepriseEntretienRepository.add(new EntrepriseEntretien(e, passwordHash, "", "", "", "", "", u, rs, si));
    }

    /** TODO:
     * @Transactional
    public long ajouterAgentProfessionnel(String username, String email, String password,
                                          String raisonSociale, String siret) {
    }**/
}
