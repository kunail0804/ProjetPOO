package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.Camion;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.model.Louable.Moto;
import com.delorent.model.Louable.SqlClause;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.Voiture;
import com.delorent.repository.RepositoryBase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VehiculeRepository implements RepositoryBase<VehiculeSummary, Integer> {

    private final JdbcTemplate jdbcTemplate;
    // On garde ces repositories car 'getByProprietaire' (branche Parking) en a besoin
    private final VoitureRepository voitureRepository;
    private final CamionRepository camionRepository;
    private final MotoRepository motoRepository;

    public VehiculeRepository(JdbcTemplate jdbcTemplate,
                              VoitureRepository voitureRepository,
                              CamionRepository camionRepository,
                              MotoRepository motoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.voitureRepository = voitureRepository;
        this.camionRepository = camionRepository;
        this.motoRepository = motoRepository;
    }

    // ========= NOUVELLE MÉTHODE PUISSANTE (Apport US.L.10) =========
    public List<VehiculeSummary> getCatalogue(LocalDate date, boolean uniquementDisponibles, List<LouableFiltre> filtres) {
        if (date == null) date = LocalDate.now();

        String existsDispo =
                "EXISTS (" +
                "  SELECT 1 FROM DISPONIBILITE d " +
                "  WHERE d.idLouable = l.id " +
                "    AND d.estReservee = 0 " +
                "    AND DATE(d.dateDebut) <= ? " +
                "    AND DATE(d.dateFin) >= ? " +
                ")";

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append(" l.id AS idLouable, ")
           .append(" l.prixJour AS prixJour, ")
           .append(" l.lieuPrincipal AS lieuPrincipal, ")
           .append(" l.statut AS statut_db, ")
           .append(" l.marque AS marque, ")
           .append(" v.modele AS modele, ")
           .append(" v.annee AS annee, ")
           .append(" v.couleur AS couleur, ")
           .append(" v.immatriculation AS immatriculation, ")
           .append(" v.kilometrage AS kilometrage, ")
           .append(" CASE ")
           .append("   WHEN m.id IS NOT NULL THEN 'Moto' ")
           .append("   WHEN ca.id IS NOT NULL THEN 'Camion' ")
           .append("   WHEN vo.id IS NOT NULL THEN 'Voiture' ")
           .append("   ELSE 'Voiture' ")
           .append(" END AS type_vehicule, ")
           .append(" CASE WHEN ").append(existsDispo).append(" THEN 'DISPONIBLE' ELSE 'INDISPONIBLE' END AS statut_calc ")
           .append("FROM LOUABLE l ")
           .append("JOIN VEHICULE v ON l.id = v.id ")
           .append("LEFT JOIN MOTO m ON v.id = m.id ")
           .append("LEFT JOIN CAMION ca ON v.id = ca.id ")
           .append("LEFT JOIN VOITURE vo ON v.id = vo.id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Paramètres pour le CASE WHEN EXISTS (statut_calc)
        params.add(Date.valueOf(date));
        params.add(Date.valueOf(date));

        // filtres génériques (prix max)
        for (LouableFiltre filtre : filtres) {
            if (filtre != null && filtre.isActif()) {
                SqlClause clause = filtre.toSqlClause();
                if (clause != null && clause.getPredicate() != null && !clause.getPredicate().isBlank()) {
                    sql.append(" AND ").append(clause.getPredicate()).append(" ");
                    params.addAll(clause.getParams());
                }
            }
        }

        // filtre uniquement dispo
        if (uniquementDisponibles) {
            sql.append(" AND ").append(existsDispo).append(" ");
            params.add(Date.valueOf(date));
            params.add(Date.valueOf(date));
        }

        sql.append(" ORDER BY l.id ASC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            int idLouable = rs.getInt("idLouable");
            double prixJour = rs.getDouble("prixJour");
            String lieuPrincipal = rs.getString("lieuPrincipal");

            String statutCalc = rs.getString("statut_calc");
            StatutLouable statut = StatutLouable.fromDb(statutCalc);

            String typeVehicule = rs.getString("type_vehicule");

            LouableSummary louable = new LouableSummary(
                    idLouable,
                    statut,
                    prixJour,
                    lieuPrincipal,
                    typeVehicule
            );

            return new VehiculeSummary(
                    louable,
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getInt("annee"),
                    rs.getString("couleur"),
                    rs.getString("immatriculation"),
                    rs.getInt("kilometrage"),
                    typeVehicule
            );
        });
    }

    // ========= MÉTHODES STANDARD =========

    @Override
    public List<VehiculeSummary> getAll() {
        // On utilise la nouvelle méthode puissante par défaut
        return getCatalogue(LocalDate.now(), false, List.of());
    }

    @Override
    public VehiculeSummary get(Integer id) {
        // On utilise la version SQL optimisée de US.L.10
        List<VehiculeSummary> res = jdbcTemplate.query(
                "SELECT l.id AS idLouable, l.prixJour, l.lieuPrincipal, l.statut AS statut_db, l.marque, " +
                "v.modele, v.annee, v.couleur, v.immatriculation, v.kilometrage, " +
                "CASE WHEN m.id IS NOT NULL THEN 'Moto' WHEN ca.id IS NOT NULL THEN 'Camion' WHEN vo.id IS NOT NULL THEN 'Voiture' ELSE 'Voiture' END AS type_vehicule " +
                "FROM LOUABLE l " +
                "JOIN VEHICULE v ON l.id=v.id " +
                "LEFT JOIN MOTO m ON v.id=m.id " +
                "LEFT JOIN CAMION ca ON v.id=ca.id " +
                "LEFT JOIN VOITURE vo ON v.id=vo.id " +
                "WHERE l.id = ?",
                new Object[]{id},
                (rs, rowNum) -> {
                    String type = rs.getString("type_vehicule");
                    StatutLouable statut = StatutLouable.fromDb(rs.getString("statut_db"));
                    LouableSummary louable = new LouableSummary(
                            rs.getInt("idLouable"),
                            statut,
                            rs.getDouble("prixJour"),
                            rs.getString("lieuPrincipal"),
                            type
                    );
                    return new VehiculeSummary(
                            louable,
                            rs.getString("marque"),
                            rs.getString("modele"),
                            rs.getInt("annee"),
                            rs.getString("couleur"),
                            rs.getString("immatriculation"),
                            rs.getInt("kilometrage"),
                            type
                    );
                }
        );
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Integer add(VehiculeSummary entity) {
        throw new UnsupportedOperationException("Use specific repositories to add vehicles.");
    }

    @Override
    public boolean modify(VehiculeSummary entity) {
        throw new UnsupportedOperationException("Use specific repositories to modify vehicles.");
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Use specific repositories to delete vehicles.");
    }

    public List<VehiculeSummary> getDisponibles(List<LouableFiltre> filtres) {
        return getCatalogue(LocalDate.now(), true, filtres);
    }

    // ========= MÉTHODES CONSERVÉES DE LA BRANCHE PARKING (HEAD) =========
    // Ces méthodes utilisent les sous-repositories et sont vitales pour votre fonctionnalité Parking

    private boolean isDisponibleAujourdhui(int idLouable, StatutLouable statut) {
        if (statut == null || statut != StatutLouable.DISPONIBLE) return false;
        Integer exists = jdbcTemplate.queryForObject(
                "SELECT CASE WHEN EXISTS (" +
                        " SELECT 1 FROM DISPONIBILITE d" +
                        " WHERE d.idLouable = ?" +
                        "   AND d.estReservee = 0" +
                        "   AND CURDATE() BETWEEN DATE(d.dateDebut) AND DATE(d.dateFin)" +
                        ") THEN 1 ELSE 0 END",
                Integer.class, idLouable
        );
        return exists != null && exists == 1;
    }

    private LouableSummary toLouableSummary(int idLouable, int idAgent, StatutLouable statut,
                                           double prixJour, String lieuPrincipal, String type) {
        boolean dispoToday = isDisponibleAujourdhui(idLouable, statut);
        return new LouableSummary(idLouable, idAgent, statut, prixJour, lieuPrincipal, type, dispoToday);
    }

    public List<VehiculeSummary> getByProprietaire(int idProprietaire) {
        List<Voiture> voitures = voitureRepository.getByProprietaire(idProprietaire);
        List<Camion> camions = camionRepository.getByProprietaire(idProprietaire);
        List<Moto> motos = motoRepository.getByProprietaire(idProprietaire);

        List<VehiculeSummary> summaries = new ArrayList<>();
        
        // Helper interne pour mapper proprement sans dupliquer le code
        for (Voiture v : voitures) {
            summaries.add(new VehiculeSummary(
                toLouableSummary(v.getIdLouable(), v.getIdAgent(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), "Voiture"),
                v.getMarque(), v.getModele(), v.getAnnee(), v.getCouleur(), v.getImmatriculation(), v.getKilometrage(), "Voiture"
            ));
        }
        for (Camion v : camions) {
            summaries.add(new VehiculeSummary(
                toLouableSummary(v.getIdLouable(), v.getIdAgent(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), "Camion"),
                v.getMarque(), v.getModele(), v.getAnnee(), v.getCouleur(), v.getImmatriculation(), v.getKilometrage(), "Camion"
            ));
        }
        for (Moto v : motos) {
            summaries.add(new VehiculeSummary(
                toLouableSummary(v.getIdLouable(), v.getIdAgent(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), "Moto"),
                v.getMarque(), v.getModele(), v.getAnnee(), v.getCouleur(), v.getImmatriculation(), v.getKilometrage(), "Moto"
            ));
        }
        return summaries;
    }
}