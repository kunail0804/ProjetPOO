package com.delorent.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Nouveau

import com.delorent.model.Assurance;
import com.delorent.model.Contrat;
import com.delorent.model.Louable.Disponibilite;
import com.delorent.model.OffreConvoyage; // Import Nouveau
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
    private final OffreConvoyageRepository offreRepo; // 1. Injection du nouveau repo

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

        // --- Vos vérifications de base (On garde tout !) ---
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

        // --- 2. LOGIQUE ALLER SIMPLE (Insertion de la nouveauté ici) ---
        OffreConvoyage offre = offreRepo.getByLouable(idLouable);
        Integer idParkingRetour = null;
        
        String lieuPrise = (louable.lieuPrincipal() == null) ? "" : louable.lieuPrincipal();
        String lieuDepot;

        if (offre != null) {
            // Cas : Offre active -> On force le parking
            idParkingRetour = offre.getIdParkingArrivee();
            lieuDepot = "Parking Partenaire Vienci (" + offre.getVilleParking() + ")";
        } else {
            // Cas : Classique (Code d'origine)
            lieuDepot = (lieuDepotOptionnel == null || lieuDepotOptionnel.trim().isEmpty())
                    ? lieuPrise
                    : lieuDepotOptionnel.trim();
        }

        // --- Vos vérifications de disponibilité (On garde votre méthode optimisée !) ---
        Disponibilite dispoCouvrante = dispoRepo.findOneCoveringRange(idLouable, dateDebut, dateFin);
        if (dispoCouvrante == null) {
            throw new IllegalArgumentException("Ce véhicule n'est pas disponible sur toute la période demandée.");
        }

        if (contratRepo.contratChevauche(idLouable, dateDebut, dateFin)) {
            throw new IllegalArgumentException("Conflit : un contrat existe déjà sur tout ou partie de ces dates.");
        }

        // --- 3. Création du Contrat (Modification pour utiliser 'add' et inclure le parking) ---
        
        Contrat contrat = new Contrat();
        contrat.setDateDebut(dateDebut);
        contrat.setDateFin(dateFin);
        contrat.setLieuPrise(lieuPrise);
        contrat.setLieuDepot(lieuDepot);
        contrat.setIdLoueur(idLoueur);
        contrat.setIdLouable(idLouable);
        contrat.setIdAssurance(idAssurance);
        contrat.setIdParkingRetour(idParkingRetour); // La nouvelle info

        // On utilise .add() car c'est la seule méthode qui gère la colonne idParkingRetour
        // (Votre méthode createContrat ne la gère pas, donc on bascule sur add pour cette fonctionnalité)
        int idContrat = contratRepo.add(contrat);
        contrat.setId(idContrat);

        // --- Gestion de la disponibilité (On garde votre logique de découpage) ---
        int idDispo = dispoCouvrante.getIdDisponibilite();
        LocalDate dispoDebut = dispoCouvrante.getDateDebut();
        LocalDate dispoFin = dispoCouvrante.getDateFin();

        dispoRepo.delete(idDispo);

        if (dispoDebut.isBefore(dateDebut)) {
            LocalDate leftEnd = dateDebut.minusDays(1);
            if (!leftEnd.isBefore(dispoDebut)) {
                dispoRepo.add(new Disponibilite(idLouable, dispoDebut, leftEnd));
            }
        }

        if (dateFin.isBefore(dispoFin)) {
            LocalDate rightStart = dateFin.plusDays(1);
            if (!dispoFin.isBefore(rightStart)) {
                dispoRepo.add(new Disponibilite(idLouable, rightStart, dispoFin));
            }
        }

        return contrat;
    }

    // --- Helpers (On rajoute juste l'offre, on garde le reste) ---

    public OffreConvoyage getOffreActive(int idLouable) {
        return offreRepo.getByLouable(idLouable);
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