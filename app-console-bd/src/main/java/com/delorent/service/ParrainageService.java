package com.delorent.service;

import com.delorent.model.Parrainage;
import com.delorent.repository.ParrainageRepository;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.LoueurRepository; // adapte tên repo của m
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ParrainageService {

    private final ParrainageRepository parrainageRepo;
    private final LoueurRepository loueurRepo;
    private final ContratRepository contratService; 

    public ParrainageService(ParrainageRepository parrainageRepo,
                             LoueurRepository loueurRepo,
                             ContratRepository contratService) {
        this.parrainageRepo = parrainageRepo;
        this.loueurRepo = loueurRepo;
        this.contratService = contratService;
    }

    public void parrainer(int idParrain, int idFilleul) {
        if (idParrain == idFilleul) {
            throw new IllegalArgumentException("Impossible de se parrainer soi-même.");
        }
        if (parrainageRepo.existsByFilleul(idFilleul)) {
            throw new IllegalArgumentException("Ce loueur a déjà un parrain.");
        }
        parrainageRepo.create(idParrain, idFilleul);
    }

    /**
     * Gọi sau khi filleul tạo/confirm 1 contrat.
     * Nếu filleul đã có >=1 contrat và parrainage đang EN_ATTENTE -> cộng tiền + validate.
     */
    @Transactional
    public void checkAndRewardAfterFirstRental(int idFilleul) {
        Optional<Parrainage> pending = parrainageRepo.findPendingByFilleul(idFilleul);
        if (pending.isEmpty()) return;

        int nbContrats = contratService.countContratsByLoueur(idFilleul);
        if (nbContrats >= 1) {
            Parrainage p = pending.get();
            loueurRepo.addCredit(p.getIdParrain(), ReferralConstants.REWARD_AMOUNT);
            parrainageRepo.validate(p.getIdParrainage());
        }
    }
}
