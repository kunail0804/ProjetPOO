package com.delorent.repository.LouableRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import com.delorent.model.Louable.Vehicule;
import com.delorent.model.Louable.Camion;
import com.delorent.model.Louable.Voiture;
import com.delorent.model.Louable.Moto;
import com.delorent.model.Louable.LouableFiltre;

import com.delorent.repository.RepositoryBase;

import java.time.LocalDate;
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
        return getCatalogue(LocalDate.now(), false, List.of());
    }

    @Override
    public VehiculeSummary get(Integer id) {
        // inchangé (mais attention: si VOITURE/CAMION/MOTO vides, tu ne trouveras pas)
        try {
            Voiture voiture = voitureRepository.get(id);
            return voitureRepository.toSummary(voiture, true); // bool ici non utilisé, mais méthode attend un boolean
        } catch (Exception ignored) {}

        try {
            Camion camion = camionRepository.get(id);
            return camionRepository.toSummary(camion, true);
        } catch (Exception ignored) {}

        try {
            Moto moto = motoRepository.get(id);
            return motoRepository.toSummary(moto, true);
        } catch (Exception ignored) {}

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

    public List<VehiculeSummary> getCatalogue(LocalDate dateCible, boolean uniquementDisponibles, List<LouableFiltre> filtres) {

        List<Voiture> voitures = voitureRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);
        List<Camion> camions = camionRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);
        List<Moto> motos = motoRepository.getCatalogue(dateCible, uniquementDisponibles, filtres);

        List<VehiculeSummary> out = new ArrayList<>();

        // Les repo renvoient des entités où le champ statut est celui de base.
        // Le "disponibleLeJour" est calculé dans le SELECT et injecté dans les Summary via toSummary.
        for (Voiture v : voitures) out.add(voitureRepository.toSummary(v, voitureRepository.isDernierDisponibleLeJour(v)));
        for (Camion c : camions) out.add(camionRepository.toSummary(c, camionRepository.isDernierDisponibleLeJour(c)));
        for (Moto m : motos) out.add(motoRepository.toSummary(m, motoRepository.isDernierDisponibleLeJour(m)));

        return out;
    }
}