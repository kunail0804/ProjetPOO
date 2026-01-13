package com.delorent.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delorent.model.Utilisateur.Agent;
import com.delorent.model.Utilisateur.EntrepriseEntretien;
import com.delorent.model.Utilisateur.Loueur;
import com.delorent.repository.AgentRepository;
import com.delorent.repository.EntrepriseEntretienRepository;
import com.delorent.repository.LoueurRepository;

@Service
public class UtilisateurService {

    private final AgentRepository agentRepository;
    private final LoueurRepository loueurRepository;
    private final EntrepriseEntretienRepository entrepriseEntretienRepository;

    public UtilisateurService(
            AgentRepository agentRepository,
            LoueurRepository loueurRepository,
            EntrepriseEntretienRepository entrepriseEntretienRepository
    ) {
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
        // TODO: BCryptPasswordEncoder etc.
        return rawPassword;
    }

    // --- Méthodes demandées (ordre des args = constructeurs des modèles) ---

    @Transactional
    public long ajouterAgent(
            String email,
            String password,
            String adresse,
            String ville,
            String codePostal,
            String region,
            String telephone,
            String nom,
            String prenom
    ) {
        String e = clean(email);
        String p = clean(password);
        String a = clean(adresse);
        String v = clean(ville);
        String cp = clean(codePostal);
        String r = clean(region);
        String t = clean(telephone);
        String n = clean(nom);
        String pr = clean(prenom);

        require(e, "email");
        require(p, "password");
        require(a, "adresse");
        require(v, "ville");
        require(cp, "codePostal");
        require(r, "region");
        require(t, "telephone");
        require(n, "nom");
        require(pr, "prenom");

        String passwordHash = hashPassword(p);

        // Agent(String mail, String motDePasse, String adresse, String ville, String codePostal, String region, String telephone, String nom, String prenom)
        return agentRepository.add(new Agent(e, passwordHash, a, v, cp, r, t, n, pr));
    }

    @Transactional
    public long ajouterLoueur(
            String email,
            String password,
            String adresse,
            String ville,
            String codePostal,
            String region,
            String telephone,
            String nom,
            String prenom
    ) {
        String e = clean(email);
        String p = clean(password);
        String a = clean(adresse);
        String v = clean(ville);
        String cp = clean(codePostal);
        String r = clean(region);
        String t = clean(telephone);
        String n = clean(nom);
        String pr = clean(prenom);

        require(e, "email");
        require(p, "password");
        require(a, "adresse");
        require(v, "ville");
        require(cp, "codePostal");
        require(r, "region");
        require(t, "telephone");
        require(n, "nom");
        require(pr, "prenom");

        String passwordHash = hashPassword(p);

        // Loueur(String mail, String motDePasse, String adresse, String ville, String codePostal, String region, String telephone, String nom, String prenom)
        return loueurRepository.add(new Loueur(e, passwordHash, a, v, cp, r, t, n, pr));
    }

    @Transactional
    public long ajouterEntrepriseEntretien(
            String email,
            String password,
            String adresse,
            String ville,
            String codePostal,
            String region,
            String telephone,
            String nomEntreprise,
            String raisonSociale,
            String siret
    ) {
        String e = clean(email);
        String p = clean(password);
        String a = clean(adresse);
        String v = clean(ville);
        String cp = clean(codePostal);
        String r = clean(region);
        String t = clean(telephone);
        String ne = clean(nomEntreprise);
        String rs = clean(raisonSociale);
        String si = clean(siret);

        require(e, "email");
        require(p, "password");
        require(a, "adresse");
        require(v, "ville");
        require(cp, "codePostal");
        require(r, "region");
        require(t, "telephone");
        require(ne, "nomEntreprise");
        require(rs, "raisonSociale");
        require(si, "siret");

        String passwordHash = hashPassword(p);

        // EntrepriseEntretien(String mail, String motDePasse, String adresse, String ville, String codePostal, String region, String telephone, String nomEntreprise, String raisonSoc, String noSiret)
        return entrepriseEntretienRepository.add(new EntrepriseEntretien(e, passwordHash, a, v, cp, r, t, ne, rs, si));
    }

    //TODO : corriger la suppression dans les repositorys

    @Transactional
    public void supprimerAgent(Long idAgent) {
        agentRepository.delete(idAgent);
    }

    @Transactional
    public void supprimerLoueur(Long idLoueur) {
        loueurRepository.delete(idLoueur);
    }

    @Transactional
    public void supprimerEntrepriseEntretien(Long idEntreprise) {
        entrepriseEntretienRepository.delete(idEntreprise);
    }

    @Transactional
    public void updateAgent(Agent agent) {
        agentRepository.modify(agent);
    }

    @Transactional
    public void updateLoueur(Loueur loueur) {
        loueurRepository.modify(loueur);
    }

    @Transactional
    public void updateEntrepriseEntretien(EntrepriseEntretien entrepriseEntretien) {
        entrepriseEntretienRepository.modify(entrepriseEntretien);
    }

    /** TODO:
     * @Transactional
     * public long ajouterAgentProfessionnel(...) { ... }
     */
}
