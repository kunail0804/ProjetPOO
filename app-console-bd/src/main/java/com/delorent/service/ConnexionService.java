package com.delorent.service;

import com.delorent.model.Utilisateur;
import com.delorent.repository.AgentRepository;
import com.delorent.repository.EntrepriseEntretienRepository;
import com.delorent.repository.LoueurRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConnexionService {

    private final AgentRepository agentRepository;
    private final LoueurRepository loueurRepository;
    private final EntrepriseEntretienRepository entrepriseEntretienRepository;

    // ✅ stocké par session (car bean session-scoped)
    private Utilisateur utilisateurConnecte;

    public ConnexionService(
            AgentRepository agentRepository,
            LoueurRepository loueurRepository,
            EntrepriseEntretienRepository entrepriseEntretienRepository
    ) {
        this.agentRepository = agentRepository;
        this.loueurRepository = loueurRepository;
        this.entrepriseEntretienRepository = entrepriseEntretienRepository;
    }

    /**
     * Retourne l'utilisateur actuellement connecté (ou null si personne).
     */
    public Utilisateur getConnexion() {
        return utilisateurConnecte;
    }

    public boolean estConnecte() {
        return utilisateurConnecte != null;
    }

    public void deconnecter() {
        utilisateurConnecte = null;
    }

    /**
     * Auth "simple": vérifie email+password dans la table correspondant au rôle.
     * ⚠️ Si tu hashes les mots de passe (recommandé), remplace l'égalité directe par une vérif hash.
     *
     * @throws IllegalArgumentException si credentials invalides
     */
    public Utilisateur connecter(String role, String email, String password) {
        if (role == null || role.isBlank()) throw new IllegalArgumentException("Rôle manquant.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email manquant.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Mot de passe manquant.");

        Utilisateur found = switch (role) {
            case "AGENT" -> agentRepository.findByEmailAndPassword(email, password);
            case "LOUEUR" -> loueurRepository.findByEmailAndPassword(email, password);
            case "ENTRETIEN" -> entrepriseEntretienRepository.findByEmailAndPassword(email, password);
            default -> throw new IllegalArgumentException("Rôle inconnu: " + role);
        };

        if (found == null) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        utilisateurConnecte = found;
        return found;
    }

    /**
     * Rafraîchit l'utilisateur connecté en mémoire après une modification.
     */
    public void refreshConnexion(Long idUtilisateur) {
        if (utilisateurConnecte == null || utilisateurConnecte.getIdUtilisateur() != idUtilisateur.intValue()) {
            return; // pas connecté ou pas le bon utilisateur
        }

        Utilisateur refreshed = null;

        if (utilisateurConnecte instanceof com.delorent.model.Agent) {
            refreshed = agentRepository.get(idUtilisateur);
        } else if (utilisateurConnecte instanceof com.delorent.model.Loueur) {
            refreshed = loueurRepository.get(idUtilisateur);
        } else if (utilisateurConnecte instanceof com.delorent.model.EntrepriseEntretien) {
            refreshed = entrepriseEntretienRepository.get(idUtilisateur);
        }

        if (refreshed != null) {
            utilisateurConnecte = refreshed;
        }
    }
}
