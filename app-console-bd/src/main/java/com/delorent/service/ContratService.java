package com.delorent.service;

import com.delorent.model.ReleveKM.ReleveKilometrage;
import com.delorent.model.ReleveKM.ReleveKilometrage;
import com.delorent.repository.ContratRepository;
import com.delorent.model.ReleveKM.ReleveType;
import com.delorent.repository.ReleveKilometrageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ContratService {

    private final ContratRepository contratRepo;
    private final ReleveKilometrageRepository releveRepo;

    public ContratService(ContratRepository contratRepo, ReleveKilometrageRepository releveRepo) {
        this.contratRepo = contratRepo;
        this.releveRepo = releveRepo;
    }

    @Transactional(readOnly = true)
    public List<ReleveKilometrage> getRelevesContrat(int idContrat, int idLoueur) {
        assertContratAppartientAuLoueur(idContrat, idLoueur);
        return releveRepo.getByContrat(idContrat);
    }

    @Transactional(readOnly = true)
    public boolean peutSaisirType(int idContrat, int idLoueur, ReleveType type) {
        assertContratAppartientAuLoueur(idContrat, idLoueur);
        if (type == null) return false;

        // max 2 relevés
        if (releveRepo.countByContrat(idContrat) >= 2) return false;

        // pas de doublon par type
        return !releveRepo.existsType(idContrat, type.name());
    }

    @Transactional
    public void saisirReleve(int idContrat,
                             int idLoueur,
                             ReleveType type,
                             int kilometrage,
                             MultipartFile photo) {

        assertContratAppartientAuLoueur(idContrat, idLoueur);

        if (type == null) {
            throw new IllegalArgumentException("Type de relevé manquant.");
        }
        if (kilometrage < 0) {
            throw new IllegalArgumentException("Le kilométrage ne peut pas être négatif.");
        }
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo obligatoire (preuve).");
        }

        // règles métier (même si tu as aussi un trigger)
        int count = releveRepo.countByContrat(idContrat);
        if (count >= 2) {
            throw new IllegalArgumentException("Il y a déjà 2 relevés pour ce contrat.");
        }
        if (releveRepo.existsType(idContrat, type.name())) {
            throw new IllegalArgumentException("Un relevé " + type.name() + " existe déjà pour ce contrat.");
        }

        String photoPath = savePhotoToUploads(idContrat, type, photo);

        ReleveKilometrage r = new ReleveKilometrage(
                0,
                idContrat,
                type,
                kilometrage,
                photoPath,
                LocalDateTime.now()
        );

        releveRepo.add(r);
    }

    private void assertContratAppartientAuLoueur(int idContrat, int idLoueur) {
        if (!contratRepo.contratAppartientAuLoueur(idContrat, idLoueur)) {
            throw new IllegalArgumentException("Accès refusé : ce contrat ne vous appartient pas.");
        }
    }

    private String savePhotoToUploads(int idContrat, ReleveType type, MultipartFile photo) {
        // sécurité simple sur content-type
        String ct = photo.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new IllegalArgumentException("La photo doit être une image.");
        }

        String original = photo.getOriginalFilename();
        String ext = guessExtension(original);
        String filename = type.name().toLowerCase() + "-" + UUID.randomUUID() + ext;

        Path dir = Paths.get("uploads", "releves-km", String.valueOf(idContrat));
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(filename);

            // overwrite interdit par défaut : on force REPLACE_EXISTING si besoin
            Files.copy(photo.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // chemin web
            return "/uploads/releves-km/" + idContrat + "/" + filename;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'enregistrer la photo : " + e.getMessage(), e);
        }
    }

    private String guessExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return "";
        String ext = filename.substring(dot).toLowerCase();
        // mini whitelist
        return switch (ext) {
            case ".jpg", ".jpeg", ".png", ".webp" -> ext;
            default -> "";
        };
    }
}