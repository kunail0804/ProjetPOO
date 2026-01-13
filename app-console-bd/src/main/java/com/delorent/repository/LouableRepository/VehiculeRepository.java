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

    // ========= VERSION FINALE (Affichage + Filtre Date) =========
    public List<VehiculeSummary> getCatalogue(LocalDate date, boolean uniquementDisponibles, List<LouableFiltre> filtres) {
        if (date == null) date = LocalDate.now();
        Date sqlDate = Date.valueOf(date);

        // Cette requête vérifie si une dispo existe pour la date choisie
        String dispoSql = """
            (SELECT COUNT(*) FROM DISPONIBILITE d 
             WHERE d.idLouable = l.id 
             AND d.estReservee = 0 
             AND ? BETWEEN DATE(d.dateDebut) AND DATE(d.dateFin)) > 0
        """;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT l.id AS idLouable, l.prixJour, l.lieuPrincipal, l.statut, ")
           .append("v.marque, v.modele, v.annee, v.couleur, v.immatriculation, v.kilometrage, ")
           .append(dispoSql).append(" AS est_dispo ") // On récupère 1 (vrai) ou 0 (faux)
           .append("FROM LOUABLE l ")
           .append("JOIN VEHICULE v ON l.id = v.id ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        params.add(sqlDate); // Pour le paramètre '?' dans dispoSql

        // Filtre "Uniquement disponibles"
        if (uniquementDisponibles) {
            sql.append(" AND ").append(dispoSql);
            params.add(sqlDate); // On rajoute le paramètre une 2ème fois car on répète la condition
        }

        // Autres filtres (Prix Max, etc.)
        for (LouableFiltre filtre : filtres) {
            if (filtre != null && filtre.isActif()) {
                SqlClause clause = filtre.toSqlClause();
                if (clause != null && !clause.getPredicate().isBlank()) {
                    sql.append(" AND ").append(clause.getPredicate()).append(" ");
                    params.addAll(clause.getParams());
                }
            }
        }

        sql.append(" ORDER BY l.id ASC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            boolean isDispo = rs.getBoolean("est_dispo");

            LouableSummary louable = new LouableSummary(
                    rs.getInt("idLouable"),
                    0, 
                    StatutLouable.valueOf(rs.getString("statut")),
                    rs.getDouble("prixJour"),
                    rs.getString("lieuPrincipal"),
                    "Voiture", 
                    isDispo // ✅ C'est ici que la magie opère pour le badge VERT/ROUGE
            );

            return new VehiculeSummary(
                    louable,
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getInt("annee"),
                    rs.getString("couleur"),
                    rs.getString("immatriculation"),
                    rs.getInt("kilometrage"),
                    "Voiture"
            );
        });
    }

    // ========= LE REST NE CHANGE PAS =========

    @Override
    public List<VehiculeSummary> getAll() {
        return getCatalogue(LocalDate.now(), false, List.of());
    }

    @Override
    public VehiculeSummary get(Integer id) {
        String sql = "SELECT l.*, v.* FROM LOUABLE l JOIN VEHICULE v ON l.id = v.id WHERE l.id = ?";
        List<VehiculeSummary> res = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
             LouableSummary louable = new LouableSummary(
                    rs.getInt("id"),
                    0,
                    StatutLouable.valueOf(rs.getString("statut")),
                    rs.getDouble("prixJour"),
                    rs.getString("lieuPrincipal"),
                    "Voiture",
                    false
            );
            return new VehiculeSummary(
                    louable, rs.getString("marque"), rs.getString("modele"),
                    rs.getInt("annee"), rs.getString("couleur"), rs.getString("immatriculation"),
                    rs.getInt("kilometrage"), "Voiture"
            );
        });
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public Integer add(VehiculeSummary entity) { throw new UnsupportedOperationException(); }
    @Override
    public boolean modify(VehiculeSummary entity) { throw new UnsupportedOperationException(); }
    @Override
    public boolean delete(Integer id) { throw new UnsupportedOperationException(); }

    public List<VehiculeSummary> getDisponibles(List<LouableFiltre> filtres) {
        return getCatalogue(LocalDate.now(), true, filtres);
    }

    // Méthode helper pour la compatibilité (branche Parking)
    private LouableSummary toLouableSummary(int idLouable, int idAgent, StatutLouable statut,
                                           double prixJour, String lieuPrincipal, String type) {
        return new LouableSummary(idLouable, idAgent, statut, prixJour, lieuPrincipal, type, true);
    }

    public List<VehiculeSummary> getByProprietaire(int idProprietaire) {
        List<Voiture> voitures = voitureRepository.getByProprietaire(idProprietaire);
        List<Camion> camions = camionRepository.getByProprietaire(idProprietaire);
        List<Moto> motos = motoRepository.getByProprietaire(idProprietaire);

        List<VehiculeSummary> summaries = new ArrayList<>();
        for (Voiture v : voitures) summaries.add(wrap(v, "Voiture"));
        for (Camion v : camions) summaries.add(wrap(v, "Camion"));
        for (Moto v : motos) summaries.add(wrap(v, "Moto"));
        return summaries;
    }
    
    // Petit helper pour éviter de dupliquer le code dans getByProprietaire
    private VehiculeSummary wrap(com.delorent.model.Louable.Vehicule v, String type) {
         return new VehiculeSummary(
                toLouableSummary(v.getIdLouable(), v.getIdAgent(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), type),
                v.getMarque(), v.getModele(), v.getAnnee(), v.getCouleur(), v.getImmatriculation(), v.getKilometrage(), type
         );
    }
}