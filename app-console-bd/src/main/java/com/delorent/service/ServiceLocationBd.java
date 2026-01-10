package com.delorent.service;

import com.delorent.model.Contrat;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class ServiceLocationBd implements ServiceLocation {

    private final JdbcTemplate jdbc;

    public ServiceLocationBd(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public Contrat louer(int idLoueur,
                         int idLouable,
                         int idAssurance,
                         LocalDate dateDebut,
                         LocalDate dateFin,
                         String lieuDepotOptionnel) {

        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de location sont obligatoires.");
        }
        if (!dateFin.isAfter(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }

        // Existence loueur / louable / assurance (TON schéma)
        Integer loueurOk = jdbc.queryForObject(
                "SELECT COUNT(*) FROM LOUEUR WHERE idUtilisateur = ?",
                Integer.class, idLoueur
        );
        if (loueurOk == null || loueurOk == 0) {
            throw new IllegalArgumentException("Loueur introuvable : id=" + idLoueur);
        }

        Integer louableOk = jdbc.queryForObject(
                "SELECT COUNT(*) FROM LOUABLE WHERE id = ?",
                Integer.class, idLouable
        );
        if (louableOk == null || louableOk == 0) {
            throw new IllegalArgumentException("Véhicule/louable introuvable : id=" + idLouable);
        }

        Integer assuranceOk = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ASSURANCE WHERE idAssurance = ?",
                Integer.class, idAssurance
        );
        if (assuranceOk == null || assuranceOk == 0) {
            throw new IllegalArgumentException("Assurance introuvable : id=" + idAssurance);
        }

        // Lieu de prise = LOUABLE.lieuPrincipal (NON modifiable)
        String lieuPrise = jdbc.queryForObject(
                "SELECT lieuPrincipal FROM LOUABLE WHERE id = ?",
                String.class, idLouable
        );
        if (lieuPrise == null) lieuPrise = "";

        String lieuDepot = (lieuDepotOptionnel != null && !lieuDepotOptionnel.isBlank())
                ? lieuDepotOptionnel.trim()
                : lieuPrise;

        // On travaille en intervalles [debut, fin) à minuit
        Timestamp debutTs = Timestamp.valueOf(dateDebut.atStartOfDay());
        Timestamp finTs = Timestamp.valueOf(dateFin.atStartOfDay());

        // 1) Vérifier qu'il existe un créneau DISPONIBILITE couvrant la période (non réservé)
        List<Map<String, Object>> slots = jdbc.queryForList(
                """
                SELECT idDisponibilite, dateDebut, dateFin, estReservee, prixJournalier
                FROM DISPONIBILITE
                WHERE idLouable = ?
                  AND estReservee = 0
                  AND dateDebut <= ?
                  AND dateFin >= ?
                ORDER BY dateDebut ASC
                LIMIT 1
                """,
                idLouable, debutTs, finTs
        );

        if (slots.isEmpty()) {
            throw new IllegalStateException("Aucune disponibilité ne couvre cette période pour ce véhicule.");
        }

        Map<String, Object> slot = slots.get(0);
        int idDisponibilite = ((Number) slot.get("idDisponibilite")).intValue();
        Timestamp slotStart = (Timestamp) slot.get("dateDebut");
        Timestamp slotEnd = (Timestamp) slot.get("dateFin");
        Double prixJournalierSlot = slot.get("prixJournalier") == null ? null : ((Number) slot.get("prixJournalier")).doubleValue();

        // 2) Vérifier chevauchement avec contrats existants (TON schéma en DATE)
        Integer chevauchements = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM CONTRAT
                WHERE idLouable = ?
                  AND dateDebut < ?
                  AND dateFin > ?
                """,
                Integer.class,
                idLouable, java.sql.Date.valueOf(dateFin), java.sql.Date.valueOf(dateDebut)
        );

        if (chevauchements != null && chevauchements > 0) {
            throw new IllegalStateException("Le véhicule est déjà loué sur cette période.");
        }

        // 3) Prix estimé (LOUABLE.prixJour + ASSURANCE.tarifJournalier) * nbJours
        Double prixJour = jdbc.queryForObject(
                "SELECT prixJour FROM LOUABLE WHERE id = ?",
                Double.class, idLouable
        );
        Double tarifAssurance = jdbc.queryForObject(
                "SELECT tarifJournalier FROM ASSURANCE WHERE idAssurance = ?",
                Double.class, idAssurance
        );
        if (prixJour == null) prixJour = 0.0;
        if (tarifAssurance == null) tarifAssurance = 0.0;

        long nbJours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (nbJours <= 0) nbJours = 1;

        double prixEstime = (prixJour + tarifAssurance) * nbJours;

        // 4) Insertion CONTRAT (TON schéma)
        try {
            jdbc.update(
                    """
                    INSERT INTO CONTRAT(dateDebut, dateFin, lieuPrise, lieuDepot, idLoueur, idLouable, idAssurance)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """,
                    java.sql.Date.valueOf(dateDebut),
                    java.sql.Date.valueOf(dateFin),
                    lieuPrise,
                    lieuDepot,
                    idLoueur,
                    idLouable,
                    idAssurance
            );
        } catch (DataAccessException e) {
            throw new IllegalStateException("Erreur lors de la création du contrat : " + rootMessage(e));
        }

        // 5) Split du créneau DISPONIBILITE :
        //    - supprimer le slot d'origine
        //    - créer [slotStart, debutTs) si besoin
        //    - créer [debutTs, finTs) réservé
        //    - créer [finTs, slotEnd) si besoin
        // Ordre IMPORTANT pour ne pas déclencher le trigger de chevauchement.
        jdbc.update("DELETE FROM DISPONIBILITE WHERE idDisponibilite = ?", idDisponibilite);

        if (slotStart.before(debutTs)) {
            jdbc.update(
                    """
                    INSERT INTO DISPONIBILITE(idLouable, dateDebut, dateFin, estReservee, prixJournalier)
                    VALUES (?, ?, ?, 0, ?)
                    """,
                    idLouable, slotStart, debutTs, prixJournalierSlot
            );
        }

        jdbc.update(
                """
                INSERT INTO DISPONIBILITE(idLouable, dateDebut, dateFin, estReservee, prixJournalier)
                VALUES (?, ?, ?, 1, ?)
                """,
                idLouable, debutTs, finTs, prixJournalierSlot
        );

        if (finTs.before(slotEnd)) {
            jdbc.update(
                    """
                    INSERT INTO DISPONIBILITE(idLouable, dateDebut, dateFin, estReservee, prixJournalier)
                    VALUES (?, ?, ?, 0, ?)
                    """,
                    idLouable, finTs, slotEnd, prixJournalierSlot
            );
        }

        return new Contrat(dateDebut, dateFin, lieuPrise, lieuDepot, prixEstime);
    }

    private static String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage() == null ? cur.getClass().getSimpleName() : cur.getMessage();
    }
}