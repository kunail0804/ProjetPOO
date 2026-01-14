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

    @Override
    public List<VehiculeSummary> getAll() {

        List<VehiculeSummary> result = new ArrayList<>();

        List<Voiture> voitures = voitureRepository.getAll();
        List<Camion> camions = camionRepository.getAll();
        List<Moto> motos = motoRepository.getAll();

        for (Voiture v : voitures) {
            result.add(new VehiculeSummary(
                new LouableSummary(v.getIdLouable(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), "Voiture"),
                v.getMarque(),
                v.getModele(),
                v.getAnnee(),
                v.getCouleur(),
                v.getImmatriculation(),
                v.getKilometrage(),
                "Voiture"
            ));
        }

        for (Camion c : camions) {
            result.add(new VehiculeSummary(
                new LouableSummary(c.getIdLouable(), c.getStatut(), c.getPrixJour(), c.getLieuPrincipal(), "Camion"),
                c.getMarque(),
                c.getModele(),
                c.getAnnee(),
                c.getCouleur(),
                c.getImmatriculation(),
                c.getKilometrage(),
                "Camion"
            ));
        }

        for (Moto m : motos) {
            result.add(new VehiculeSummary(
                new LouableSummary(m.getIdLouable(), m.getStatut(), m.getPrixJour(), m.getLieuPrincipal(), "Moto"),
                m.getMarque(),
                m.getModele(),
                m.getAnnee(),
                m.getCouleur(),
                m.getImmatriculation(),
                m.getKilometrage(),
                "Moto"
            ));
        }

        return result;
    }

    @Override
    public VehiculeSummary get(Integer id) {

        boolean voitureFail = false;
        boolean camionFail = false;
        boolean motoFail = false;

        try {
            Voiture voiture = voitureRepository.get(id);
            if (voiture != null) {
                return new VehiculeSummary(
                    new LouableSummary(voiture.getIdLouable(), voiture.getStatut(), voiture.getPrixJour(), voiture.getLieuPrincipal(), "Voiture"),
                    voiture.getMarque(),
                    voiture.getModele(),
                    voiture.getAnnee(),
                    voiture.getCouleur(),
                    voiture.getImmatriculation(),
                    voiture.getKilometrage(),
                    "Voiture"
                );
            }
        } catch (Exception e) {
            voitureFail = true;
        }

        try {
            Camion camion = camionRepository.get(id);
            if (camion != null) {
                return new VehiculeSummary(
                    new LouableSummary(camion.getIdLouable(), camion.getStatut(), camion.getPrixJour(), camion.getLieuPrincipal(), "Voiture"),
                    camion.getMarque(),
                    camion.getModele(),
                    camion.getAnnee(),
                    camion.getCouleur(),
                    camion.getImmatriculation(),
                    camion.getKilometrage(),
                    "Camion"
                );
            }
        } catch (Exception e) {
            camionFail = true;
        }

        try {
            Moto moto = motoRepository.get(id);
            if (moto != null) {
                return new VehiculeSummary(
                    new LouableSummary(moto.getIdLouable(), moto.getStatut(), moto.getPrixJour(), moto.getLieuPrincipal(), "Voiture"),
                    moto.getMarque(),
                    moto.getModele(),
                    moto.getAnnee(),
                    moto.getCouleur(),
                    moto.getImmatriculation(),
                    moto.getKilometrage(),
                    "Moto"
                );
            }
        } catch (Exception e) {
            motoFail = true;
        }

        if (voitureFail && camionFail && motoFail) {
            return null; // ou throw new NoSuchElementException("Véhicule introuvable id=" + id);
        }

        return null;
    }

    @Override
    public Integer add(VehiculeSummary entity) { throw new UnsupportedOperationException(); }
    @Override
    public boolean modify(VehiculeSummary entity) { throw new UnsupportedOperationException(); }
    @Override
    public boolean delete(Integer id) { throw new UnsupportedOperationException(); }

    public List<VehiculeSummary> getDisponibles(List<LouableFiltre> filtres) {

        List<VehiculeSummary> result = new ArrayList<>();

        List<Voiture> voitures = new ArrayList<>();
        List<Camion> camions = new ArrayList<>();
        List<Moto> motos = new ArrayList<>();

        try {
            voitures = voitureRepository.getDisponibles(filtres);
        } catch (Exception ignored) {}

        try {
            camions = camionRepository.getDisponibles(filtres);
        } catch (Exception ignored) {}

        try {
            motos = motoRepository.getDisponibles(filtres);
        } catch (Exception ignored) {}

        for (Voiture v : voitures) {
            result.add(new VehiculeSummary(
                new LouableSummary(v.getIdLouable(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), "Voiture"),
                v.getMarque(),
                v.getModele(),
                v.getAnnee(),
                v.getCouleur(),
                v.getImmatriculation(),
                v.getKilometrage(),
                "Voiture"
            ));
        }

        for (Camion c : camions) {
            result.add(new VehiculeSummary(
                new LouableSummary(c.getIdLouable(), c.getStatut(), c.getPrixJour(), c.getLieuPrincipal(), "Camion"),
                c.getMarque(),
                c.getModele(),
                c.getAnnee(),
                c.getCouleur(),
                c.getImmatriculation(),
                c.getKilometrage(),
                "Camion"
            ));
        }

        for (Moto m : motos) {
            result.add(new VehiculeSummary(
                new LouableSummary(m.getIdLouable(), m.getStatut(), m.getPrixJour(), m.getLieuPrincipal(), "Moto"),
                m.getMarque(),
                m.getModele(),
                m.getAnnee(),
                m.getCouleur(),
                m.getImmatriculation(),
                m.getKilometrage(),
                "Moto"
            ));
        }

        return result;
    }

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