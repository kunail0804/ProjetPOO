package com.delorent.service;

import com.delorent.model.Contrat;
import com.delorent.model.ContratEtat;
import com.delorent.model.Assurance;
import com.delorent.model.Disponibilite;
import com.delorent.repository.AssuranceRepository;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.DisponibiliteRepository;
import com.delorent.repository.LouableRepository;
import com.delorent.repository.LouableSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LocationService {

    private final LouableRepository louableRepo;
    private final AssuranceRepository assuranceRepo;
    private final ContratRepository contratRepo;
    private final DisponibiliteRepository dispoRepo;

    public LocationService(LouableRepository louableRepo,
                           AssuranceRepository assuranceRepo,
                           ContratRepository contratRepo,
                           DisponibiliteRepository dispoRepo) {
        this.louableRepo = louableRepo;
        this.assuranceRepo = assuranceRepo;
        this.contratRepo = contratRepo;
        this.dispoRepo = dispoRepo;
    }

    @Transactional
    public Contrat louer(int idLoueur,
                         int idLouable,
                         int idAssurance,
                         LocalDate dateDebut,
                         LocalDate dateFin,
                         String lieuDepotOptionnel) {

        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Dates manquantes.");
        }
        if (!dateDebut.isBefore(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être strictement avant la date de fin.");
        }

        LouableSummary louable = louableRepo.get(idLouable);
        if (louable == null) {
            throw new IllegalArgumentException("Louable introuvable (id=" + idLouable + ").");
        }

        Assurance assurance = assuranceRepo.get(idAssurance);
        if (assurance == null) {
            throw new IllegalArgumentException("Assurance introuvable (id=" + idAssurance + ").");
        }

        String lieuPrise = (louable.lieuPrincipal() == null) ? "" : louable.lieuPrincipal();
        String lieuDepot = (lieuDepotOptionnel == null || lieuDepotOptionnel.trim().isEmpty())
                ? lieuPrise
                : lieuDepotOptionnel.trim();

        // dispo couvrante
        Disponibilite dispoCouvrante = dispoRepo.findOneCoveringRange(idLouable, dateDebut, dateFin);
        if (dispoCouvrante == null) {
            throw new IllegalArgumentException("Ce véhicule n'est pas disponible sur toute la période demandée.");
        }

        // pas de chevauchement contrat
        if (contratRepo.contratChevauche(idLouable, dateDebut, dateFin)) {
            throw new IllegalArgumentException("Conflit : un contrat existe déjà sur tout ou partie de ces dates.");
        }

        // créer contrat
        int idContrat = contratRepo.createContrat(
                dateDebut, dateFin,
                lieuPrise, lieuDepot,
                idLoueur, idLouable, idAssurance
        );

        // split dispo couvrante
        int idDispo = dispoCouvrante.getIdDisponibilite();
        LocalDate dispoDebut = dispoCouvrante.getDateDebut();
        LocalDate dispoFin = dispoCouvrante.getDateFin();

        dispoRepo.delete(idDispo);

        // gauche : dispoDebut -> (dateDebut - 1)
        if (dispoDebut.isBefore(dateDebut)) {
            LocalDate leftEnd = dateDebut.minusDays(1);
            if (!leftEnd.isBefore(dispoDebut)) {
                dispoRepo.add(new Disponibilite(idLouable, dispoDebut, leftEnd));
            }
        }

        // droite : (dateFin + 1) -> dispoFin
        if (dateFin.isBefore(dispoFin)) {
            LocalDate rightStart = dateFin.plusDays(1);
            if (!dispoFin.isBefore(rightStart)) {
                dispoRepo.add(new Disponibilite(idLouable, rightStart, dispoFin));
            }
        }

        // renvoyer le Contrat (sans recharge BD, suffisant pour afficher)
        return new Contrat(idContrat, dateDebut, dateFin, lieuPrise, lieuDepot);
    }

    @Transactional
    public LouableSummary getLouable(int idLouable) {
        return louableRepo.get(idLouable);
    }

    @Transactional
    public List<Assurance> getAllAssurances() {
        return assuranceRepo.getAll();
    }

    @Transactional
    public List<Disponibilite> getByLouable(int idLouable) {
        return dispoRepo.getByLouable(idLouable);
    }

    @Transactional
    public Assurance getAssurance(int idAssurance) {
        return assuranceRepo.get(idAssurance);
    }
}
