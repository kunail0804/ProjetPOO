package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.Camion;
import com.delorent.model.Louable.LouableFiltre;
import com.delorent.model.Louable.Moto;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.Voiture;
import com.delorent.repository.RepositoryBase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
                toLouableSummary(v.getIdLouable(), v.getIdAgent(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), "Voiture"),
                v.getMarque(), v.getModele(), v.getAnnee(), v.getCouleur(), v.getImmatriculation(), v.getKilometrage(), "Voiture"
            ));
        }

        for (Camion c : camions) {
            result.add(new VehiculeSummary(
                toLouableSummary(c.getIdLouable(), c.getIdAgent(), c.getStatut(), c.getPrixJour(), c.getLieuPrincipal(), "Camion"),
                c.getMarque(), c.getModele(), c.getAnnee(), c.getCouleur(), c.getImmatriculation(), c.getKilometrage(), "Camion"
            ));
        }

        for (Moto m : motos) {
            result.add(new VehiculeSummary(
                toLouableSummary(m.getIdLouable(), m.getIdAgent(), m.getStatut(), m.getPrixJour(), m.getLieuPrincipal(), "Moto"),
                m.getMarque(), m.getModele(), m.getAnnee(), m.getCouleur(), m.getImmatriculation(), m.getKilometrage(), "Moto"
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
                    toLouableSummary(voiture.getIdLouable(), voiture.getIdAgent(), voiture.getStatut(), voiture.getPrixJour(), voiture.getLieuPrincipal(), "Voiture"),
                    voiture.getMarque(), voiture.getModele(), voiture.getAnnee(), voiture.getCouleur(), voiture.getImmatriculation(), voiture.getKilometrage(), "Voiture"
                );
            }
        } catch (Exception e) {
            voitureFail = true;
        }

        try {
            Camion camion = camionRepository.get(id);
            if (camion != null) {
                return new VehiculeSummary(
                    toLouableSummary(camion.getIdLouable(), camion.getIdAgent(), camion.getStatut(), camion.getPrixJour(), camion.getLieuPrincipal(), "Camion"),
                    camion.getMarque(), camion.getModele(), camion.getAnnee(), camion.getCouleur(), camion.getImmatriculation(), camion.getKilometrage(), "Camion"
                );
            }
        } catch (Exception e) {
            camionFail = true;
        }

        try {
            Moto moto = motoRepository.get(id);
            if (moto != null) {
                return new VehiculeSummary(
                    toLouableSummary(moto.getIdLouable(), moto.getIdAgent(), moto.getStatut(), moto.getPrixJour(), moto.getLieuPrincipal(), "Moto"),
                    moto.getMarque(), moto.getModele(), moto.getAnnee(), moto.getCouleur(), moto.getImmatriculation(), moto.getKilometrage(), "Moto"
                );
            }
        } catch (Exception e) {
            motoFail = true;
        }

        if (voitureFail && camionFail && motoFail) {
            return null;
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

        try { voitures = voitureRepository.getDisponibles(filtres); } catch (Exception ignored) {}
        try { camions = camionRepository.getDisponibles(filtres); } catch (Exception ignored) {}
        try { motos = motoRepository.getDisponibles(filtres); } catch (Exception ignored) {}

        for (Voiture v : voitures) {
            result.add(new VehiculeSummary(
                toLouableSummary(v.getIdLouable(), v.getIdAgent(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), "Voiture"),
                v.getMarque(), v.getModele(), v.getAnnee(), v.getCouleur(), v.getImmatriculation(), v.getKilometrage(), "Voiture"
            ));
        }

        for (Camion c : camions) {
            result.add(new VehiculeSummary(
                toLouableSummary(c.getIdLouable(), c.getIdAgent(), c.getStatut(), c.getPrixJour(), c.getLieuPrincipal(), "Camion"),
                c.getMarque(), c.getModele(), c.getAnnee(), c.getCouleur(), c.getImmatriculation(), c.getKilometrage(), "Camion"
            ));
        }

        for (Moto m : motos) {
            result.add(new VehiculeSummary(
                toLouableSummary(m.getIdLouable(), m.getIdAgent(), m.getStatut(), m.getPrixJour(), m.getLieuPrincipal(), "Moto"),
                m.getMarque(), m.getModele(), m.getAnnee(), m.getCouleur(), m.getImmatriculation(), m.getKilometrage(), "Moto"
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

    private VehiculeSummary wrap(com.delorent.model.Louable.Vehicule v, String type) {
         return new VehiculeSummary(
                toLouableSummary(v.getIdLouable(), v.getIdAgent(), v.getStatut(), v.getPrixJour(), v.getLieuPrincipal(), type),
                v.getMarque(), v.getModele(), v.getAnnee(), v.getCouleur(), v.getImmatriculation(), v.getKilometrage(), type
         );
    }
}