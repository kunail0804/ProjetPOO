package com.delorent.service;

import com.delorent.repository.NoteRepository;
import com.delorent.repository.ContratRepository;
import com.delorent.model.Contrat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service pour gérer la logique métier des notations.
 * 
 * Ce service contient toutes les règles métier :
 * - Calcul de la note globale (moyenne des critères)
 * - Validation des données
 * - Vérification qu'un contrat n'est pas noté deux fois
 * - Vérification que le contrat appartient bien au loueur
 * 
 * US.L.3 - Noter un véhicule
 */
@Service
public class NoteService {
    
    // Repository pour accéder aux données des notes
    private final NoteRepository noteRepository;
    
    // Repository pour accéder aux données des contrats (créé par tes collègues)
    private final ContratRepository contratRepository;
    
    // Injection des dépendances via le constructeur
    public NoteService(NoteRepository noteRepository, ContratRepository contratRepository) {
        this.noteRepository = noteRepository;
        this.contratRepository = contratRepository;
    }
    
    /**
     * Vérifie si un contrat appartient à un loueur donné.
     * 
     * Cette vérification est importante pour la sécurité : on ne veut pas
     * qu'un loueur puisse noter le contrat d'un autre loueur !
     * 
     * @param idContrat L'ID du contrat à vérifier
     * @param idLoueur L'ID du loueur connecté
     * @return true si le contrat appartient au loueur, false sinon
     */
    public boolean contratAppartientALoueur(int idContrat, int idLoueur) {
        // On utilise le ContratRepository existant pour récupérer les contrats du loueur
        List<Contrat> contratsLoueur = contratRepository.getByLoueurId(idLoueur);
        
        // On cherche si le contrat demandé est dans la liste
        for (Contrat contrat : contratsLoueur) {
            if (contrat.getId() == idContrat) {
                return true;  // Le contrat appartient bien à ce loueur
            }
        }
        
        return false;  // Le contrat n'appartient pas à ce loueur
    }
    
    /**
     * Récupère tous les critères de notation.
     * 
     * Délègue au Repository qui retourne les critères depuis la BDD
     * (ou des données mock si on est en mode test).
     * 
     * @return Liste des critères (id, libelle)
     */
    public List<Map<String, Object>> getCriteres() {
        return noteRepository.findAllCriteres();
    }
    
    /**
     * Récupère les informations d'un contrat pour l'affichage dans le formulaire.
     * 
     * @param idContrat L'ID du contrat
     * @return Les informations du contrat (véhicule, dates, lieu) ou null si non trouvé
     */
    public Map<String, Object> getContrat(int idContrat) {
        return noteRepository.findContratById(idContrat);
    }
    
    /**
     * Vérifie si un contrat peut être noté.
     * 
     * Un contrat peut être noté si :
     * - Il n'a pas déjà été noté
     * 
     * @param idContrat L'ID du contrat à vérifier
     * @return true si le contrat peut être noté, false sinon
     */
    public boolean peutEtreNote(int idContrat) {
        // On vérifie que le contrat n'a pas déjà été noté
        return !noteRepository.contratDejaNote(idContrat);
    }
    
    /**
     * Calcule la note globale à partir des notes individuelles.
     * 
     * La note globale est simplement la MOYENNE de toutes les notes de critères.
     * Par exemple, si l'utilisateur a donné : 5, 4, 4, 3, 4
     * La moyenne sera : (5 + 4 + 4 + 3 + 4) / 5 = 4.0
     * 
     * @param notesCriteres Map contenant les notes (clé = idCritere, valeur = note)
     * @return La moyenne arrondie à une décimale
     */
    public double calculerNoteGlobale(Map<Integer, Integer> notesCriteres) {
        
        // Si la map est vide, on retourne 0 pour éviter une division par zéro
        if (notesCriteres == null || notesCriteres.isEmpty()) {
            return 0.0;
        }
        
        // On calcule la somme de toutes les notes
        double somme = 0.0;
        for (Integer note : notesCriteres.values()) {
            somme += note;
        }
        
        // On calcule la moyenne
        double moyenne = somme / notesCriteres.size();
        
        // On arrondit à une décimale (ex: 4.333... devient 4.3)
        // Math.round(x * 10) / 10.0 est une astuce classique pour arrondir à 1 décimale
        return Math.round(moyenne * 10.0) / 10.0;
    }
    
    /**
     * Enregistre une note complète (note globale + notes par critère + commentaire).
     * 
     * Cette méthode orchestre tout le processus de notation :
     * 1. Vérifie que le contrat peut être noté
     * 2. Valide les notes (entre 1 et 5)
     * 3. Calcule la note globale
     * 4. Sauvegarde tout dans la base de données
     * 
     * @param idContrat L'ID du contrat concerné
     * @param notesCriteres Les notes données pour chaque critère
     * @param commentaire Le commentaire textuel (peut être vide)
     * @return true si la note a été enregistrée, false si le contrat était déjà noté
     * @throws IllegalArgumentException si les notes ne sont pas valides
     */
    public boolean enregistrerNote(int idContrat, Map<Integer, Integer> notesCriteres, 
                                    String commentaire) {
        
        // Étape 1 : Vérifier que le contrat n'a pas déjà été noté
        if (!peutEtreNote(idContrat)) {
            return false;  // Le contrat a déjà été noté, on refuse
        }
        
        // Étape 2 : Valider que toutes les notes sont entre 1 et 5
        for (Map.Entry<Integer, Integer> entry : notesCriteres.entrySet()) {
            int note = entry.getValue();
            if (note < 1 || note > 5) {
                throw new IllegalArgumentException(
                    "La note doit être comprise entre 1 et 5. Reçu : " + note
                );
            }
        }
        
        // Étape 3 : Calculer la note globale (la moyenne)
        double noteGlobale = calculerNoteGlobale(notesCriteres);
        
        // Étape 4 : Sauvegarder dans la base de données (ou en mémoire si mock)
        noteRepository.sauvegarderNote(idContrat, noteGlobale, commentaire, notesCriteres);
        
        return true;  // Succès !
    }
    
    /**
     * Récupère toutes les notes d'un véhicule.
     * 
     * @param idLouable L'ID du véhicule
     * @return Liste des notes avec commentaires et date
     */
    public List<Map<String, Object>> getNotesByVehicule(int idLouable) {
        return noteRepository.findNotesByVehicule(idLouable);
    }
    
    /**
     * Calcule la note moyenne d'un véhicule à partir de toutes ses notes.
     * 
     * Cette méthode est utile pour afficher la note globale d'un véhicule
     * sur sa page de détail (moyenne de toutes les notes reçues).
     * 
     * @param idLouable L'ID du véhicule
     * @return La moyenne des notes globales, ou 0 si aucune note
     */
    public double getNoteMoyenneVehicule(int idLouable) {
        List<Map<String, Object>> notes = noteRepository.findNotesByVehicule(idLouable);
        
        if (notes == null || notes.isEmpty()) {
            return 0.0;
        }
        
        double somme = 0.0;
        for (Map<String, Object> note : notes) {
            somme += (Double) note.get("noteGlobale");
        }
        
        double moyenne = somme / notes.size();
        return Math.round(moyenne * 10.0) / 10.0;
    }
}
