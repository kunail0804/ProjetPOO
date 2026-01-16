package com.delorent.service;

import com.delorent.model.Louable.Disponibilite;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.DisponibiliteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DisponibiliteAdminService {

    private final DisponibiliteRepository dispoRepo;
    private final ContratRepository contratRepo;

    public DisponibiliteAdminService(DisponibiliteRepository dispoRepo, ContratRepository contratRepo) {
        this.dispoRepo = dispoRepo;
        this.contratRepo = contratRepo;
    }

    private void validateDates(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Dates manquantes.");
        }
        if (fin.isBefore(debut)) {
            throw new IllegalArgumentException("La date de fin doit être >= la date de début.");
        }
    }

    @Transactional(readOnly = true)
    public List<Disponibilite> getByLouable(int idLouable) {
        return dispoRepo.getByLouable(idLouable);
    }

    @Transactional
    public void ajouterDisponibilite(int idLouable, LocalDate debut, LocalDate fin) {
        validateDates(debut, fin);

        if (contratRepo.contratChevauche(idLouable, debut, fin)) {
            throw new IllegalArgumentException("Impossible : un contrat existe déjà sur tout ou partie de cette période.");
        }

        dispoRepo.add(new Disponibilite(idLouable, debut, fin, false, null));
    }

    @Transactional
    public void supprimerDisponibilite(int idLouable, int idDisponibilite) {
        Disponibilite d = dispoRepo.get(idDisponibilite);
        if (d == null) throw new IllegalArgumentException("Disponibilité introuvable.");
        if (d.getIdLouable() == null || d.getIdLouable() != idLouable) {
            throw new IllegalArgumentException("Disponibilité incohérente.");
        }
        if (d.isEstReservee()) {
            throw new IllegalArgumentException("Impossible : cette période est marquée réservée.");
        }
        if (contratRepo.contratChevauche(idLouable, d.getDateDebut(), d.getDateFin())) {
            throw new IllegalArgumentException("Impossible : un contrat existe déjà sur tout ou partie de cette période.");
        }

        dispoRepo.delete(idDisponibilite);
    }
}