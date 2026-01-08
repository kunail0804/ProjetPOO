package com.delorent.service;

import com.delorent.model.Contrat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ServiceLocationBd implements ServiceLocation {

    private final JdbcTemplate jdbc;

    public ServiceLocationBd(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Contrat louer(int idLoueur,
                         int idLouable,
                         int idAssurance,
                         LocalDate dateDebut,
                         LocalDate dateFin,
                         String lieuDepotOptionnel) {

        // 0) Validations
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de location sont obligatoires.");
        }
        if (!dateFin.isAfter(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début.");
        }

        // 1) Récupérer l'agent propriétaire (id_proprietaire) du louable
        Integer idAgent = jdbc.queryForObject(
                "SELECT id_proprietaire FROM LOUABLE WHERE idLouable = ?",
                Integer.class,
                idLouable
        );
        if (idAgent == null) {
            throw new IllegalArgumentException("Louable introuvable : idLouable=" + idLouable);
        }

        // 2) Vérifier que l'assurance est proposée par cet agent
        Integer nb = jdbc.queryForObject(
                "SELECT COUNT(*) FROM POSSEDE_ASSURANCE WHERE id_agent = ? AND id_assurance = ?",
                Integer.class,
                idAgent, idAssurance
        );
        if (nb == null || nb == 0) {
            throw new IllegalArgumentException("Assurance non disponible pour cet agent.");
        }

        // 3) Lieu de prise = lieuPrincipal du véhicule (NON modifiable)
        final String lieuPrise = jdbc.queryForObject(
                "SELECT lieuPrincipal FROM LOUABLE WHERE idLouable = ?",
                String.class,
                idLouable
        );
        if (lieuPrise == null || lieuPrise.isBlank()) {
            throw new IllegalStateException("Le véhicule n'a pas de lieuPrincipal défini (lieu de prise).");
        }

        // Lieu de dépôt : optionnel, sinon = lieuPrise
        final String lieuDepot = (lieuDepotOptionnel != null && !lieuDepotOptionnel.isBlank())
                ? lieuDepotOptionnel.trim()
                : lieuPrise;

        // 4) Vérifier chevauchement de contrats (hors ANNULE/REFUSE)
        final Timestamp debutTs = Timestamp.valueOf(dateDebut.atStartOfDay());
        final Timestamp finTs = Timestamp.valueOf(dateFin.atStartOfDay());

        Integer chevauchements = jdbc.queryForObject(
                """
                SELECT COUNT(*)
                FROM CONTRAT
                WHERE id_louable = ?
                  AND etat NOT IN ('ANNULE','REFUSE')
                  AND date_debut < ?
                  AND date_fin > ?
                """,
                Integer.class,
                idLouable, finTs, debutTs
        );

        if (chevauchements != null && chevauchements > 0) {
            throw new IllegalStateException("Le louable est déjà réservé sur cette période.");
        }

        // 5) Calcul du prix final
        Double prixJour = jdbc.queryForObject(
                "SELECT prixJour FROM LOUABLE WHERE idLouable = ?",
                Double.class,
                idLouable
        );
        Double prixAssuranceJour = jdbc.queryForObject(
                "SELECT prix_journalier FROM ASSURANCE WHERE id_assurance = ?",
                Double.class,
                idAssurance
        );

        if (prixJour == null) prixJour = 0.0;
        if (prixAssuranceJour == null) prixAssuranceJour = 0.0;

        long nbJours = ChronoUnit.DAYS.between(dateDebut, dateFin);
        if (nbJours <= 0) nbJours = 1;

        final double prixFinal = (prixJour + prixAssuranceJour) * nbJours;

        // 6) Insertion du contrat (on renseigne lieu_prise et lieu_depot)
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    """
                    INSERT INTO CONTRAT(
                        id_loueur, id_agent, id_louable, id_assurance,
                        date_debut, date_fin, etat, prix_final,
                        lieu_prise, lieu_depot
                    )
                    VALUES (?, ?, ?, ?, ?, ?, 'EN_ATTENTE', ?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, idLoueur);
            ps.setInt(2, idAgent);
            ps.setInt(3, idLouable);
            ps.setInt(4, idAssurance);
            ps.setTimestamp(5, debutTs);
            ps.setTimestamp(6, finTs);
            ps.setDouble(7, prixFinal);
            ps.setString(8, lieuPrise);
            ps.setString(9, lieuDepot);
            return ps;
        }, keyHolder);

        Integer idContrat = null;
        if (keyHolder.getKey() != null) {
            idContrat = keyHolder.getKey().intValue();
        }

        // 7) Retour modèle
        return new Contrat(
                idContrat,
                Date.valueOf(dateDebut),
                Date.valueOf(dateFin),
                prixFinal,
                "EN_ATTENTE",
                lieuPrise,
                lieuDepot
        );
    }
}