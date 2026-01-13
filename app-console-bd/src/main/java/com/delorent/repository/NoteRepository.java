package com.delorent.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Repository pour gérer les notes des véhicules.
 * 
 * Gère trois tables :
 * - CRITERE : les critères de notation prédéfinis
 * - NOTES : les notes globales avec commentaires
 * - NOTE_CRITERE : les notes individuelles par critère
 * 
 * ============================================================
 * MODE ACTUEL : MOCK (données fictives pour tester sans BDD)
 * 
 * Pour passer en mode BDD réelle :
 * 1. Décommenter les méthodes "VERSION BDD"
 * 2. Commenter ou supprimer les méthodes "VERSION MOCK"
 * 3. S'assurer que les critères sont insérés dans la table CRITERE
 * ============================================================
 */
@Repository
public class NoteRepository {
    
    private final JdbcTemplate jdbc;
    
    // ========== DONNÉES MOCK (simulation de la BDD) ==========
    // Ces listes stockent les données en mémoire pour les tests
    // Attention : les données sont perdues quand l'application redémarre !
    private static List<Map<String, Object>> notesSauvegardees = new ArrayList<>();
    private static int prochainIdNote = 1;
    
    public NoteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    
    // ==========================================================
    //                    VERSION MOCK
    //     (Données fictives pour tester sans base de données)
    // ==========================================================
    
    /**
     * VERSION MOCK : Retourne des critères fictifs.
     */
    public List<Map<String, Object>> findAllCriteres() {
        List<Map<String, Object>> criteres = new ArrayList<>();
        
        Map<String, Object> critere1 = new LinkedHashMap<>();
        critere1.put("id", 1);
        critere1.put("libelle", "Propreté");
        criteres.add(critere1);
        
        Map<String, Object> critere2 = new LinkedHashMap<>();
        critere2.put("id", 2);
        critere2.put("libelle", "État général");
        criteres.add(critere2);
        
        Map<String, Object> critere3 = new LinkedHashMap<>();
        critere3.put("id", 3);
        critere3.put("libelle", "Confort");
        criteres.add(critere3);
        
        Map<String, Object> critere4 = new LinkedHashMap<>();
        critere4.put("id", 4);
        critere4.put("libelle", "Conformité à l'annonce");
        criteres.add(critere4);
        
        Map<String, Object> critere5 = new LinkedHashMap<>();
        critere5.put("id", 5);
        critere5.put("libelle", "Rapport qualité/prix");
        criteres.add(critere5);
        
        return criteres;
    }
    
    /**
     * VERSION MOCK : Simule la sauvegarde d'une note en mémoire.
     */
    public void sauvegarderNote(int idContrat, double noteGlobale, String commentaire, 
                                 Map<Integer, Integer> notesCriteres) {
        
        Map<String, Object> nouvelleNote = new LinkedHashMap<>();
        nouvelleNote.put("id", prochainIdNote++);
        nouvelleNote.put("idContrat", idContrat);
        nouvelleNote.put("noteGlobale", noteGlobale);
        nouvelleNote.put("commentaire", commentaire);
        nouvelleNote.put("dateEvaluation", Date.valueOf(LocalDate.now()));
        nouvelleNote.put("notesCriteres", notesCriteres);
        
        notesSauvegardees.add(nouvelleNote);
        
        // Affichage console pour vérifier que ça fonctionne
        System.out.println("===========================================");
        System.out.println("NOTE SAUVEGARDÉE (version mock) :");
        System.out.println("  - ID Contrat : " + idContrat);
        System.out.println("  - Note globale : " + noteGlobale);
        System.out.println("  - Commentaire : " + commentaire);
        System.out.println("  - Notes par critère : " + notesCriteres);
        System.out.println("===========================================");
    }
    
    /**
     * VERSION MOCK : Vérifie si un contrat a déjà été noté (en mémoire).
     */
    public boolean contratDejaNote(int idContrat) {
        for (Map<String, Object> note : notesSauvegardees) {
            if ((int) note.get("idContrat") == idContrat) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * VERSION MOCK : Retourne les informations d'un contrat fictif.
     */
    public Map<String, Object> findContratById(int idContrat) {
        Map<String, Object> contrat = new LinkedHashMap<>();
        
        contrat.put("idContrat", idContrat);
        contrat.put("dateDebut", Date.valueOf(LocalDate.now().minusDays(7)));
        contrat.put("dateFin", Date.valueOf(LocalDate.now().minusDays(1)));
        
        // Différents véhicules selon l'ID du contrat
        if (idContrat == 1) {
            contrat.put("marque", "Peugeot");
            contrat.put("modele", "208");
            contrat.put("lieu", "Toulouse");
        } else if (idContrat == 3) {
            contrat.put("marque", "Renault");
            contrat.put("modele", "Clio");
            contrat.put("lieu", "Paris");
        } else if (idContrat == 4) {
            contrat.put("marque", "Citroën");
            contrat.put("modele", "C3");
            contrat.put("lieu", "Lyon");
        } else {
            contrat.put("marque", "Toyota");
            contrat.put("modele", "Yaris");
            contrat.put("lieu", "Bordeaux");
        }
        
        return contrat;
    }
    
    /**
     * VERSION MOCK : Retourne des notes fictives pour un véhicule.
     */
    public List<Map<String, Object>> findNotesByVehicule(int idLouable) {
        List<Map<String, Object>> notes = new ArrayList<>();
        
        Map<String, Object> note1 = new LinkedHashMap<>();
        note1.put("id", 1);
        note1.put("noteGlobale", 4.5);
        note1.put("commentaire", "Excellent véhicule, très propre !");
        note1.put("dateEvaluation", Date.valueOf(LocalDate.now().minusDays(5)));
        note1.put("nomLoueur", "Martin");
        note1.put("prenomLoueur", "Sophie");
        notes.add(note1);
        
        Map<String, Object> note2 = new LinkedHashMap<>();
        note2.put("id", 2);
        note2.put("noteGlobale", 3.8);
        note2.put("commentaire", "Bon véhicule, quelques traces d'usure.");
        note2.put("dateEvaluation", Date.valueOf(LocalDate.now().minusDays(15)));
        note2.put("nomLoueur", "Dupont");
        note2.put("prenomLoueur", "Pierre");
        notes.add(note2);
        
        return notes;
    }
    
    // ==========================================================
    //                    VERSION BDD
    //     (Décommenter quand la BDD sera prête avec les critères)
    // ==========================================================
    
    /*
    public List<Map<String, Object>> findAllCriteres() {
        String sql = "SELECT id, libelle FROM CRITERE ORDER BY id";
        
        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> critere = new LinkedHashMap<>();
            critere.put("id", rs.getInt("id"));
            critere.put("libelle", rs.getString("libelle"));
            return critere;
        });
    }
    
    public void sauvegarderNote(int idContrat, double noteGlobale, String commentaire, 
                                 Map<Integer, Integer> notesCriteres) {
        
        // Étape 1 : Insérer la note globale dans la table NOTES
        String sqlNote = "INSERT INTO NOTES (noteGlobale, commentaire, dateEvaluation, idContrat) " +
                         "VALUES (?, ?, ?, ?)";
        
        jdbc.update(sqlNote, noteGlobale, commentaire, Date.valueOf(LocalDate.now()), idContrat);
        
        // Étape 2 : Récupérer l'ID de la note qu'on vient d'insérer
        String sqlGetId = "SELECT LAST_INSERT_ID()";
        int idNote = jdbc.queryForObject(sqlGetId, Integer.class);
        
        // Étape 3 : Insérer une ligne dans NOTE_CRITERE pour chaque critère noté
        String sqlNoteCritere = "INSERT INTO NOTE_CRITERE (idNote, idCritere, valeur) VALUES (?, ?, ?)";
        
        for (Map.Entry<Integer, Integer> entry : notesCriteres.entrySet()) {
            int idCritere = entry.getKey();
            int valeur = entry.getValue();
            jdbc.update(sqlNoteCritere, idNote, idCritere, valeur);
        }
    }
    
    public boolean contratDejaNote(int idContrat) {
        String sql = "SELECT COUNT(*) FROM NOTES WHERE idContrat = ?";
        int count = jdbc.queryForObject(sql, Integer.class, idContrat);
        return count > 0;
    }
    
    public Map<String, Object> findContratById(int idContrat) {
        String sql = "SELECT c.idContrat, c.dateDebut, c.dateFin, " +
                     "       v.marque, v.modele, l.lieuPrincipal " +
                     "FROM CONTRAT c " +
                     "JOIN LOUABLE l ON c.idLouable = l.id " +
                     "JOIN VEHICULE v ON l.id = v.id " +
                     "WHERE c.idContrat = ?";
        
        return jdbc.queryForObject(sql, (rs, rowNum) -> {
            Map<String, Object> contrat = new LinkedHashMap<>();
            contrat.put("idContrat", rs.getInt("idContrat"));
            contrat.put("dateDebut", rs.getDate("dateDebut"));
            contrat.put("dateFin", rs.getDate("dateFin"));
            contrat.put("marque", rs.getString("marque"));
            contrat.put("modele", rs.getString("modele"));
            contrat.put("lieu", rs.getString("lieuPrincipal"));
            return contrat;
        }, idContrat);
    }
    
    public List<Map<String, Object>> findNotesByVehicule(int idLouable) {
        String sql = "SELECT n.id, n.noteGlobale, n.commentaire, n.dateEvaluation, " +
                     "       c.idContrat, l.nom as nomLoueur, l.prenom as prenomLoueur " +
                     "FROM NOTES n " +
                     "JOIN CONTRAT c ON n.idContrat = c.idContrat " +
                     "JOIN LOUEUR l ON c.idLoueur = l.idUtilisateur " +
                     "WHERE c.idLouable = ? " +
                     "ORDER BY n.dateEvaluation DESC";
        
        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> note = new LinkedHashMap<>();
            note.put("id", rs.getInt("id"));
            note.put("noteGlobale", rs.getDouble("noteGlobale"));
            note.put("commentaire", rs.getString("commentaire"));
            note.put("dateEvaluation", rs.getDate("dateEvaluation"));
            note.put("nomLoueur", rs.getString("nomLoueur"));
            note.put("prenomLoueur", rs.getString("prenomLoueur"));
            return note;
        }, idLouable);
    }
    */
}