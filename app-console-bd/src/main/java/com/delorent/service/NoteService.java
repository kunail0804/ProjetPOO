// FICHIER: src/main/java/com/delorent/service/NoteService.java
package com.delorent.service;

import com.delorent.model.Contrat;
import com.delorent.repository.ContratRepository;
import com.delorent.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final ContratRepository contratRepository;

    public NoteService(NoteRepository noteRepository, ContratRepository contratRepository) {
        this.noteRepository = noteRepository;
        this.contratRepository = contratRepository;
    }

    public boolean contratAppartientALoueur(int idContrat, int idLoueur) {
        List<Contrat> contratsLoueur = contratRepository.getByLoueurId(idLoueur);
        for (Contrat c : contratsLoueur) {
            if (c.getId() == idContrat) return true;
        }
        return false;
    }

    public List<Map<String, Object>> getCriteres() {
        return noteRepository.findAllCriteres();
    }

    public Map<String, Object> getContrat(int idContrat) {
        return noteRepository.findContratById(idContrat);
    }

    public boolean peutEtreNote(int idContrat) {
        return !noteRepository.contratDejaNote(idContrat);
    }

    public double calculerNoteGlobale(Map<Integer, Integer> notesCriteres) {
        if (notesCriteres == null || notesCriteres.isEmpty()) return 0.0;
        double somme = 0.0;
        for (Integer n : notesCriteres.values()) somme += n;
        double moyenne = somme / notesCriteres.size();
        return Math.round(moyenne * 10.0) / 10.0;
    }

    @Transactional
    public boolean enregistrerNote(int idContrat, Map<Integer, Integer> notesCriteres, String commentaire) {
        if (!peutEtreNote(idContrat)) return false;

        for (Map.Entry<Integer, Integer> entry : notesCriteres.entrySet()) {
            int note = entry.getValue();
            if (note < 1 || note > 5) {
                throw new IllegalArgumentException("La note doit être comprise entre 1 et 5. Reçu : " + note);
            }
        }

        double noteGlobale = calculerNoteGlobale(notesCriteres);
        noteRepository.sauvegarderNote(idContrat, noteGlobale, commentaire, notesCriteres);
        return true;
    }

    @Transactional(readOnly = true)
    public Double getNoteMoyenneVehiculeNullable(int idLouable) {
        return noteRepository.findMoyenneByLouable(idLouable);
    }
}