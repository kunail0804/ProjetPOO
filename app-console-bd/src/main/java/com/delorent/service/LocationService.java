package com.delorent.service;

import com.delorent.repository.DisponibiliteRepository;
import com.delorent.repository.LocationRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
public class LocationService {

    private final LocationRepository locationRepo;
    private final DisponibiliteRepository dispoRepo;

    public LocationService(LocationRepository locationRepo, DisponibiliteRepository dispoRepo) {
        this.locationRepo = locationRepo;
        this.dispoRepo = dispoRepo;
    }

    @Transactional
    public int creerContrat(int idLoueur, int idLouable, Integer idAssurance,
                            LocalDate dateDebut, LocalDate dateFin, String lieuDepotOptionnel) {

        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Dates manquantes.");
        }
        if (!dateDebut.isBefore(dateFin)) {
            throw new IllegalArgumentException("dateDebut doit être strictement avant dateFin.");
        }

        Map<String, Object> louable = locationRepo.getLouable(idLouable);
        if (louable == null) {
            throw new IllegalArgumentException("Louable introuvable (id=" + idLouable + ").");
        }

        String lieuPrise = (String) louable.get("lieuPrincipal");
        if (lieuPrise == null || lieuPrise.isBlank()) {
            lieuPrise = "Lieu principal non défini";
        }

        // Variante A : lieuDepot optionnel, si vide => lieuPrise
        String lieuDepot = (lieuDepotOptionnel == null || lieuDepotOptionnel.trim().isEmpty())
                ? lieuPrise
                : lieuDepotOptionnel.trim();

        // 1) vérifier qu'il existe une dispo qui couvre toute la période
        Map<String, Object> dispoCouvrante = dispoRepo.findOneCoveringRange(idLouable, dateDebut, dateFin);
        if (dispoCouvrante == null) {
            throw new IllegalArgumentException("Ce véhicule n'est pas disponible sur toute la période demandée.");
        }

        // 2) vérifier qu'aucun contrat ne chevauche
        if (locationRepo.contratChevauche(idLouable, dateDebut, dateFin)) {
            throw new IllegalArgumentException("Conflit : un contrat existe déjà sur tout ou partie de ces dates.");
        }

        // 3) créer le contrat
        int idContrat = locationRepo.insertContrat(dateDebut, dateFin, lieuPrise, lieuDepot, idLoueur, idLouable, idAssurance);

        // 4) découper la dispo couvrante (comportement propre)
        try {
            int idDispo = ((Number) dispoCouvrante.get("idDisponibilite")).intValue();
            LocalDate dispoDebut = ((java.sql.Date) dispoCouvrante.get("dateDebut")).toLocalDate();
            LocalDate dispoFin = ((java.sql.Date) dispoCouvrante.get("dateFin")).toLocalDate();

            dispoRepo.deleteById(idDispo);

            // partie gauche : dispoDebut -> (dateDebut - 1)
            if (dispoDebut.isBefore(dateDebut)) {
                LocalDate leftEnd = dateDebut.minusDays(1);
                if (dispoDebut.isBefore(leftEnd)) {
                    dispoRepo.insert(idLouable, dispoDebut, leftEnd);
                }
            }

            // partie droite : (dateFin + 1) -> dispoFin
            if (dateFin.isBefore(dispoFin)) {
                LocalDate rightStart = dateFin.plusDays(1);
                if (rightStart.isBefore(dispoFin)) {
                    dispoRepo.insert(idLouable, rightStart, dispoFin);
                }
            }

        } catch (DataAccessException e) {
            // Si ça casse ici, on rollback toute la transaction (contrat inclus)
            throw e;
        }

        return idContrat;
    }
}