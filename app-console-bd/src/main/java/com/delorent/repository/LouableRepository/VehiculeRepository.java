package com.delorent.repository.LouableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.delorent.model.Louable.Vehicule;
import com.delorent.model.Louable.Camion;
import com.delorent.model.Louable.Voiture;
import com.delorent.model.Louable.Moto;
import com.delorent.model.Louable.StatutLouable;
import com.delorent.model.Louable.LouableFiltre;

import com.delorent.repository.RepositoryBase;

import java.util.List;
import java.util.ArrayList;

@Repository
public class VehiculeRepository implements RepositoryBase<VehiculeSummary, Integer> {
    private final JdbcTemplate jdbcTemplate;
    private final VoitureRepository voitureRepository;
    private final CamionRepository camionRepository;
    private final MotoRepository motoRepository;

    public VehiculeRepository(JdbcTemplate jdbcTemplate, VoitureRepository voitureRepository, CamionRepository camionRepository, MotoRepository motoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.voitureRepository = voitureRepository;
        this.camionRepository = camionRepository;
        this.motoRepository = motoRepository;
    }

    @Override
    public List<VehiculeSummary> getAll() {
        List<Voiture> voitures = voitureRepository.getAll();
        List<Camion> camions = camionRepository.getAll();
        List<Moto> motos = motoRepository.getAll();

        List<VehiculeSummary> summaries = new ArrayList<>();
        for (Voiture voiture : voitures) {
            summaries.add(new VehiculeSummary(
                new LouableSummary(voiture.getIdLouable(), voiture.getStatut(), voiture.getPrixJour(), voiture.getLieuPrincipal(), "Voiture"),
                voiture.getMarque(),
                voiture.getModele(),
                voiture.getAnnee(),
                voiture.getCouleur(),
                voiture.getImmatriculation(),
                voiture.getKilometrage(),
                "Voiture"
            ));
        }
        for (Camion camion : camions) {
            summaries.add(new VehiculeSummary(
                new LouableSummary(camion.getIdLouable(), camion.getStatut(), camion.getPrixJour(), camion.getLieuPrincipal(), "Camion"),
                camion.getMarque(),
                camion.getModele(),
                camion.getAnnee(),
                camion.getCouleur(),
                camion.getImmatriculation(),
                camion.getKilometrage(),
                "Camion"
            ));
        }
        for (Moto moto : motos) {
            summaries.add(new VehiculeSummary(
                new LouableSummary(moto.getIdLouable(), moto.getStatut(), moto.getPrixJour(), moto.getLieuPrincipal(), "Moto"),
                moto.getMarque(),
                moto.getModele(),
                moto.getAnnee(),
                moto.getCouleur(),
                moto.getImmatriculation(),
                moto.getKilometrage(),
                "Moto"
            ));
        }
        return summaries;
    }

    @Override
    public VehiculeSummary get(Integer id) {
        try {
            Voiture voiture = voitureRepository.get(id);
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
        } catch (Exception e) {
            // Not a Voiture
        }

        try {
            Camion camion = camionRepository.get(id);
            return new VehiculeSummary(
                new LouableSummary(camion.getIdLouable(), camion.getStatut(), camion.getPrixJour(), camion.getLieuPrincipal(), "Camion"),
                camion.getMarque(),
                camion.getModele(),
                camion.getAnnee(),
                camion.getCouleur(),
                camion.getImmatriculation(),
                camion.getKilometrage(),
                "Camion"
            );
        } catch (Exception e) {
            // Not a Camion
        }

        try {
            Moto moto = motoRepository.get(id);
            return new VehiculeSummary(
                new LouableSummary(moto.getIdLouable(), moto.getStatut(), moto.getPrixJour(), moto.getLieuPrincipal(), "Moto"),
                moto.getMarque(),
                moto.getModele(),
                moto.getAnnee(),
                moto.getCouleur(),
                moto.getImmatriculation(),
                moto.getKilometrage(),
                "Moto"
            );
        } catch (Exception e) {
            // Not a Moto
        }

        return null;
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
        List<Voiture> voitures = voitureRepository.getDisponibles(filtres);
        List<Camion> camions = camionRepository.getDisponibles(filtres);
        List<Moto> motos = motoRepository.getDisponibles(filtres);
        List<VehiculeSummary> disponibles = new ArrayList<>();
        for (Voiture voiture : voitures) {
            disponibles.add(new VehiculeSummary(
                new LouableSummary(voiture.getIdLouable(), voiture.getStatut(), voiture.getPrixJour(), voiture.getLieuPrincipal(), "Voiture"),
                voiture.getMarque(),
                voiture.getModele(),
                voiture.getAnnee(),
                voiture.getCouleur(),
                voiture.getImmatriculation(),
                voiture.getKilometrage(),
                "Voiture"
            ));
        }
        for (Camion camion : camions) {
            disponibles.add(new VehiculeSummary(
                new LouableSummary(camion.getIdLouable(), camion.getStatut(), camion.getPrixJour(), camion.getLieuPrincipal(), "Camion"),
                camion.getMarque(),
                camion.getModele(),
                camion.getAnnee(),
                camion.getCouleur(),
                camion.getImmatriculation(),
                camion.getKilometrage(),
                "Camion"
            ));
        }
        for (Moto moto : motos) {
            disponibles.add(new VehiculeSummary(
                new LouableSummary(moto.getIdLouable(), moto.getStatut(), moto.getPrixJour(), moto.getLieuPrincipal(), "Moto"),
                moto.getMarque(),
                moto.getModele(),
                moto.getAnnee(),
                moto.getCouleur(),
                moto.getImmatriculation(),
                moto.getKilometrage(),
                "Moto"
            ));
        }

        return disponibles;
    }

    public List<VehiculeSummary> getByProprietaire(int idProprietaire) {
        List<Voiture> voitures = voitureRepository.getByProprietaire(idProprietaire);
        List<Camion> camions = camionRepository.getByProprietaire(idProprietaire);
        List<Moto> motos = motoRepository.getByProprietaire(idProprietaire);

        List<VehiculeSummary> summaries = new ArrayList<>();
        for (Voiture voiture : voitures) {
            summaries.add(new VehiculeSummary(
                new LouableSummary(voiture.getIdLouable(), voiture.getStatut(), voiture.getPrixJour(), voiture.getLieuPrincipal(), "Voiture"),
                voiture.getMarque(),
                voiture.getModele(),
                voiture.getAnnee(),
                voiture.getCouleur(),
                voiture.getImmatriculation(),
                voiture.getKilometrage(),
                "Voiture"
            ));
        }
        for (Camion camion : camions) {
            summaries.add(new VehiculeSummary(
                new LouableSummary(camion.getIdLouable(), camion.getStatut(), camion.getPrixJour(), camion.getLieuPrincipal(), "Camion"),
                camion.getMarque(),
                camion.getModele(),
                camion.getAnnee(),
                camion.getCouleur(),
                camion.getImmatriculation(),
                camion.getKilometrage(),
                "Camion"
            ));
        }
        for (Moto moto : motos) {
            summaries.add(new VehiculeSummary(
                new LouableSummary(moto.getIdLouable(), moto.getStatut(), moto.getPrixJour(), moto.getLieuPrincipal(), "Moto"),
                moto.getMarque(),
                moto.getModele(),
                moto.getAnnee(),
                moto.getCouleur(),
                moto.getImmatriculation(),
                moto.getKilometrage(),
                "Moto"
            ));
        }
        return summaries;
    }
}
