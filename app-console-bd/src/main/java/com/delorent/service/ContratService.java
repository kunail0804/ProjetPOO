package com.delorent.service;

import com.delorent.config.UploadProperties;
import com.delorent.model.ReleveKM.ReleveKilometrage; // Assure-toi que le package est bon
import com.delorent.model.ReleveKM.ReleveType;       // IMPORT IMPORTANT (L'Enum)
import com.delorent.repository.ContratRepository;
import com.delorent.repository.ReleveKilometrageRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ContratService {

    private final ContratRepository contratRepo;
    private final ReleveKilometrageRepository releveRepo;
    private final UploadProperties uploadProperties;
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    public ContratService(ContratRepository contratRepo, 
                          ReleveKilometrageRepository releveRepo,
                          UploadProperties uploadProperties) {
        this.contratRepo = contratRepo;
        this.releveRepo = releveRepo;
        this.uploadProperties = uploadProperties;
    }

    @Transactional(readOnly = true)
    public List<ReleveKilometrage> getRelevesContrat(int idContrat, int idLoueur) {
        verifierAppartenance(idContrat, idLoueur);
        return releveRepo.getByContrat(idContrat);
    }

    @Transactional(readOnly = true)
    public boolean peutSaisirType(int idContrat, int idLoueur, String typeStr) {
        try {
            verifierAppartenance(idContrat, idLoueur);
            if (releveRepo.countByContrat(idContrat) >= 2) return false;
            return !releveRepo.existsType(idContrat, typeStr);
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void saisirReleve(int idContrat,
                             int idLoueur,
                             String typeStr, // Reçu en String du contrôleur
                             int kilometrage,
                             MultipartFile photo) throws IOException {

        verifierAppartenance(idContrat, idLoueur);

        // 1. Validation de base
        if (typeStr == null || typeStr.isBlank()) throw new IllegalArgumentException("Type manquant.");
        if (kilometrage < 0) throw new IllegalArgumentException("Kilométrage invalide.");
        if (photo == null || photo.isEmpty()) throw new IllegalArgumentException("Photo obligatoire.");

        // 2. CONVERSION STRING -> ENUM (La correction est ici !)
        ReleveType typeEnum;
        try {
            typeEnum = ReleveType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Type de relevé invalide : " + typeStr);
        }

        // 3. Vérifications métier
        if (releveRepo.countByContrat(idContrat) >= 2) {
            throw new IllegalArgumentException("Déjà 2 relevés pour ce contrat.");
        }
        if (releveRepo.existsType(idContrat, typeStr)) {
            throw new IllegalArgumentException("Le relevé " + typeStr + " existe déjà.");
        }

        // 4. Upload
        String original = StringUtils.cleanPath(photo.getOriginalFilename() == null ? "" : photo.getOriginalFilename());
        String ext = getExtLower(original);
        if (!ALLOWED_EXT.contains(ext)) throw new IllegalArgumentException("Format non autorisé.");

        Path root = uploadProperties.getUploadRoot();
        Path dir = root.resolve("releves");
        Files.createDirectories(dir);
        
        String filename = UUID.randomUUID() + "." + ext;
        Files.copy(photo.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        String photoPath = "/uploads/releves/" + filename;

        // 5. Création de l'objet (Utilise l'Enum maintenant !)
        ReleveKilometrage r = new ReleveKilometrage();
        r.setIdContrat(idContrat);
        r.setTypeReleve(typeEnum); // ✅ On passe l'Enum, plus le String
        r.setKilometrage(kilometrage);
        r.setPhotoPath(photoPath);
        r.setDateSaisie(LocalDateTime.now());

        releveRepo.add(r);
    }

    private void verifierAppartenance(int idContrat, int idLoueur) {
        if (contratRepo.getDetailByIdAndLoueur(idContrat, idLoueur) == null) {
            throw new IllegalArgumentException("Accès refusé.");
        }
    }

    private String getExtLower(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx < 0 ? "" : filename.substring(idx + 1).toLowerCase();
    }
}