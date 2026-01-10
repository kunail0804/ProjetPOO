package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class LocationRepository {

    private final JdbcTemplate jdbc;

    public LocationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findLouablesAvecVehicule() {
        // LOUABLE.marque + VEHICULE.modele, immatriculation...
        return jdbc.queryForList("""
            SELECT
              l.id AS idLouable,
              l.marque AS marque,
              v.modele AS modele,
              v.immatriculation AS immatriculation,
              l.prixJour AS prixJour,
              l.statut AS statut,
              l.lieuPrincipal AS lieuPrincipal
            FROM LOUABLE l
            LEFT JOIN VEHICULE v ON v.id = l.id
            ORDER BY l.id ASC
        """);
    }

    public List<Map<String, Object>> findAssurances() {
        return jdbc.queryForList("""
            SELECT idAssurance, identifiant, nom, tarifJournalier
            FROM ASSURANCE
            ORDER BY tarifJournalier ASC
        """);
    }

    public Map<String, Object> getLouable(int idLouable) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT id, prixJour, statut, lieuPrincipal
            FROM LOUABLE
            WHERE id = ?
            LIMIT 1
        """, idLouable);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public boolean contratChevauche(int idLouable, LocalDate debut, LocalDate fin) {
        Integer count = jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM CONTRAT
            WHERE idLouable = ?
              AND NOT (dateFin < ? OR dateDebut > ?)
        """, Integer.class, idLouable, Date.valueOf(debut), Date.valueOf(fin));

        return count != null && count > 0;
    }

    public int insertContrat(LocalDate debut, LocalDate fin, String lieuPrise, String lieuDepot,
                            int idLoueur, int idLouable, Integer idAssurance) {
        jdbc.update("""
            INSERT INTO CONTRAT (dateDebut, dateFin, lieuPrise, lieuDepot, idLoueur, idLouable, idAssurance)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """, Date.valueOf(debut), Date.valueOf(fin), lieuPrise, lieuDepot, idLoueur, idLouable, idAssurance);

        Integer id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        return id == null ? -1 : id;
    }

    public Map<String, Object> getContrat(int idContrat) {
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT c.idContrat,
                   c.dateDebut,
                   c.dateFin,
                   c.lieuPrise,
                   c.lieuDepot,
                   c.idLoueur,
                   c.idLouable,
                   c.idAssurance,
                   a.nom AS assuranceNom
            FROM CONTRAT c
            LEFT JOIN ASSURANCE a ON a.idAssurance = c.idAssurance
            WHERE c.idContrat = ?
            LIMIT 1
        """, idContrat);

        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<Map<String, Object>> findLoueurs() {
    return jdbc.queryForList("""
        SELECT idUtilisateur, prenom, nom
        FROM LOUEUR
        ORDER BY prenom ASC, nom ASC
    """);
}
}