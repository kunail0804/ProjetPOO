package com.delorent.service;

import com.delorent.model.Louable.Disponibilite;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.DisponibiliteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DisponibiliteService {

    private final DisponibiliteRepository dispoRepo;
    private final ContratRepository contratRepo;

    public DisponibiliteService(DisponibiliteRepository dispoRepo, ContratRepository contratRepo) {
        this.dispoRepo = dispoRepo;
        this.contratRepo = contratRepo;
    }

    private void validate(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) throw new IllegalArgumentException("Dates manquantes.");
        if (fin.isBefore(debut)) throw new IllegalArgumentException("La date de fin doit être >= la date de début.");
    }

    @Transactional
    public void addOrMergeNonReservedRange(int idLouable, LocalDate debut, LocalDate fin) {
        validate(debut, fin);

        if (contratRepo.contratChevauche(idLouable, debut, fin)) {
            throw new IllegalArgumentException("Impossible: un contrat existe déjà sur tout ou partie de cette période.");
        }

        if (dispoRepo.existsOverlappingReserved(idLouable, debut, fin)) {
            throw new IllegalArgumentException("Impossible: une disponibilité réservée existe déjà sur cette période.");
        }

        List<Disponibilite> overlaps = dispoRepo.findOverlappingOrAdjacentNonReserved(idLouable, debut, fin);

        LocalDate mergedStart = debut;
        LocalDate mergedEnd = fin;

        for (Disponibilite d : overlaps) {
            if (d.getDateDebut().isBefore(mergedStart)) mergedStart = d.getDateDebut();
            if (d.getDateFin().isAfter(mergedEnd)) mergedEnd = d.getDateFin();
        }

        for (Disponibilite d : overlaps) {
            dispoRepo.delete(d.getIdDisponibilite());
        }

        dispoRepo.add(new Disponibilite(idLouable, mergedStart, mergedEnd, false, null));
    }

    @Transactional
    public void deleteRangeIfNoContrat(int idLouable, int idDisponibilite) {
        Disponibilite d = dispoRepo.get(idDisponibilite);
        if (d == null) throw new IllegalArgumentException("Disponibilité introuvable.");
        if (d.getIdLouable() == null || d.getIdLouable() != idLouable) throw new IllegalArgumentException("Disponibilité incohérente.");

        if (d.isEstReservee()) {
            throw new IllegalArgumentException("Impossible: cette période est marquée réservée.");
        }

        if (contratRepo.contratChevauche(idLouable, d.getDateDebut(), d.getDateFin())) {
            throw new IllegalArgumentException("Impossible: un contrat existe déjà sur tout ou partie de cette période.");
        }

        dispoRepo.delete(idDisponibilite);
    }

    @Transactional(readOnly = true)
    public List<Disponibilite> getByLouable(int idLouable) {
        return dispoRepo.getByLouable(idLouable);
    }
}