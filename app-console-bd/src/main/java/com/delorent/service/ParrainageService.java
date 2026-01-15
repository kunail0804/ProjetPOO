package com.delorent.service;

import com.delorent.model.Parrainage;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.LoueurRepository;
import com.delorent.repository.ParrainageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ParrainageService {

    private final ParrainageRepository parrainageRepo;
    private final LoueurRepository loueurRepo;
    private final ContratRepository contratRepo;

    public ParrainageService(ParrainageRepository parrainageRepo,
                             LoueurRepository loueurRepo,
                             ContratRepository contratRepo) {
        this.parrainageRepo = parrainageRepo;
        this.loueurRepo = loueurRepo;
        this.contratRepo = contratRepo;
    }

    /**
     * Crée un parrainage (EN_ATTENTE) seulement si:
     * - parrain et filleul existent (loueurs)
     * - pas d'auto-parrainage
     * - filleul n'a pas déjà un parrain
     * - filleul n'a encore aucun contrat (parrainage doit être avant la 1ère location)
     */
    public void parrainer(int idParrain, int idFilleul) {

        if (!loueurRepo.existsById(idParrain) || !loueurRepo.existsById(idFilleul)) {
            throw new IllegalArgumentException("Le parrainage est réservé uniquement aux loueurs.");
        }

        if (idParrain == idFilleul) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous parrainer vous-même.");
        }

        if (parrainageRepo.existsByFilleul(idFilleul)) {
            throw new IllegalArgumentException("Vous avez déjà un parrain.");
        }

        int nbContrats = contratRepo.countByLoueurId(idFilleul);
        if (nbContrats > 0) {
            throw new IllegalArgumentException("Vous ne pouvez plus être parrainé : vous avez déjà effectué une location.");
        }

        parrainageRepo.create(idParrain, idFilleul);
    }

    /**
     * À appeler après création/validation d'un contrat du filleul.
     * Si le filleul a au moins 1 contrat et un parrainage EN_ATTENTE -> on VALIDE le parrainage.
     * (Pas de crédit ici, tu as décidé de ne plus gérer le crédit.)
     */
    @Transactional
    public void checkAndValidateAfterFirstRental(int idFilleul) {

        Optional<Parrainage> pending = parrainageRepo.findPendingByFilleul(idFilleul);
        if (pending.isEmpty()) return;

        Parrainage p = pending.get();

        // sécurité (optionnel) : vérifier que les deux existent en tant que loueurs
        if (!loueurRepo.existsById(idFilleul) || !loueurRepo.existsById(p.getIdParrain())) return;

        int nbContrats = contratRepo.countByLoueurId(idFilleul);
        if (nbContrats >= 1) {
            parrainageRepo.validate(p.getIdParrainage());
        }
    }
}