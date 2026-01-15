// FICHIER: src/main/java/com/delorent/service/LocationService.java
package com.delorent.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.delorent.model.Assurance;
import com.delorent.model.Contrat;
import com.delorent.model.Louable.Disponibilite;
import com.delorent.model.OffreConvoyage;
import com.delorent.repository.AssuranceRepository;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.DisponibiliteRepository;
import com.delorent.repository.LouableRepository.LouableRepository;
import com.delorent.repository.LouableRepository.LouableSummary;
import com.delorent.repository.OffreConvoyageRepository;

@Service
public class LocationService {

    private final LouableRepository louableRepo;
    private final AssuranceRepository assuranceRepo;
    private final ContratRepository contratRepo;
    private final DisponibiliteRepository dispoRepo;
    private final OffreConvoyageRepository offreRepo;

    public LocationService(LouableRepository louableRepo,
                           AssuranceRepository assuranceRepo,
                           ContratRepository contratRepo,
                           DisponibiliteRepository dispoRepo,
                           OffreConvoyageRepository offreRepo) {
        this.louableRepo = louableRepo;
        this.assuranceRepo = assuranceRepo;
        this.contratRepo = contratRepo;
        this.dispoRepo = dispoRepo;
        this.offreRepo = offreRepo;
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
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être >= la date de début.");
        }

        LouableSummary louable = louableRepo.get(idLouable);
        if (louable == null) {
            throw new IllegalArgumentException("Louable introuvable (id=" + idLouable + ").");
        }

        Assurance assurance = assuranceRepo.get(idAssurance);
        if (assurance == null) {
            throw new IllegalArgumentException("Assurance introuvable (id=" + idAssurance + ").");
        }

        OffreConvoyage offre = offreRepo.getByLouable(idLouable);
        Integer idParkingRetour = null;

        String lieuPrise = (louable.lieuPrincipal() == null) ? "" : louable.lieuPrincipal();
        String lieuDepot;

        if (offre != null) {
            idParkingRetour = offre.getIdParkingArrivee();
            lieuDepot = "Parking Partenaire Vienci (" + offre.getVilleParking() + ")";
        } else {
            lieuDepot = (lieuDepotOptionnel == null || lieuDepotOptionnel.trim().isEmpty())
                    ? lieuPrise
                    : lieuDepotOptionnel.trim();
        }

        Disponibilite dispoCouvrante = dispoRepo.findOneCoveringRange(idLouable, dateDebut, dateFin);
        if (dispoCouvrante == null) {
            throw new IllegalArgumentException("Ce véhicule n'est pas disponible sur toute la période demandée.");
        }

        if (contratRepo.contratChevauche(idLouable, dateDebut, dateFin)) {
            throw new IllegalArgumentException("Conflit : un contrat existe déjà sur tout ou partie de ces dates.");
        }

        // ===== CALCUL PRIX (inclusif + assurance/jour + commissions) =====
        long nbJoursLong = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        if (nbJoursLong <= 0) throw new IllegalArgumentException("Durée invalide.");
        BigDecimal nbJours = BigDecimal.valueOf(nbJoursLong);

        // LouableSummary.prixJour() est un double => jamais null
        BigDecimal prixJourLouable = BigDecimal.valueOf(louable.prixJour());

        // AssuranceRepository mappe en double, donc getTarifJournalier() doit être double
        BigDecimal prixJourAssurance = BigDecimal.valueOf(assurance.getTarifJournalier());

        // base = (louable + assurance) * nbJours
        BigDecimal base = prixJourLouable.add(prixJourAssurance).multiply(nbJours);

        BigDecimal commissionVariable = base.multiply(BigDecimal.valueOf(0.10));
        BigDecimal commissionFixe = nbJours.multiply(BigDecimal.valueOf(2));

        BigDecimal prixTotal = base
                .add(commissionVariable)
                .add(commissionFixe)
                .setScale(2, RoundingMode.HALF_UP);

        // ===== INSERT CONTRAT =====
        Contrat contrat = new Contrat();
        contrat.setDateDebut(dateDebut);
        contrat.setDateFin(dateFin);
        contrat.setLieuPrise(lieuPrise);
        contrat.setLieuDepot(lieuDepot);
        contrat.setIdLoueur(idLoueur);
        contrat.setIdLouable(idLouable);
        contrat.setIdAssurance(idAssurance);
        contrat.setIdParkingRetour(idParkingRetour);

        contrat.setPrix(prixTotal);
        contrat.setEtat("accepte");

        int idContrat = contratRepo.add(contrat);
        contrat.setId(idContrat);

        // ===== DECOUPAGE DISPO =====
        int idDispo = dispoCouvrante.getIdDisponibilite();
        LocalDate dispoDebut = dispoCouvrante.getDateDebut();
        LocalDate dispoFin = dispoCouvrante.getDateFin();

        dispoRepo.delete(idDispo);

        if (dispoDebut.isBefore(dateDebut)) {
            LocalDate leftEnd = dateDebut.minusDays(1);
            if (!leftEnd.isBefore(dispoDebut)) {
                dispoRepo.add(new Disponibilite(idLouable, dispoDebut, leftEnd, false, null));
            }
        }

        if (dateFin.isBefore(dispoFin)) {
            LocalDate rightStart = dateFin.plusDays(1);
            if (!dispoFin.isBefore(rightStart)) {
                dispoRepo.add(new Disponibilite(idLouable, rightStart, dispoFin, false, null));
            }
        }

        return contrat;
    }

    @Transactional(readOnly = true)
    public OffreConvoyage getOffreActive(int idLouable) {
        return offreRepo.getByLouable(idLouable);
    }

    @Transactional(readOnly = true)
    public LouableSummary getLouable(int idLouable) {
        return louableRepo.get(idLouable);
    }

    @Transactional(readOnly = true)
    public List<Assurance> getAllAssurances() {
        return assuranceRepo.getAll();
    }

    @Transactional(readOnly = true)
    public List<Disponibilite> getByLouable(int idLouable) {
        return dispoRepo.getByLouable(idLouable);
    }

    @Transactional(readOnly = true)
    public Assurance getAssurance(int idAssurance) {
        return assuranceRepo.get(idAssurance);
    }
}