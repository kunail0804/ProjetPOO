package com.delorent.repository;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.Discussion;
import com.delorent.model.Message;

@Repository
public class MessageRepository {

    private final JdbcTemplate jdbcTemplate;

    public MessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. Trouver toutes mes discussions ET le nom de l'interlocuteur
    public List<Discussion> trouverDiscussionsUtilisateur(int monId) {
        // CORRECTION MAJEURE ICI :
        // Comme 'nom' n'est pas dans UTILISATEUR, on doit aller le chercher dans AGENT ou LOUEUR.
        // On utilise COALESCE : ça prend le premier nom non-nul qu'il trouve (Agent, sinon Loueur, sinon l'email).
        
        String sql = "SELECT " +
                     "d.idDiscussion, " +
                     "d.dateCreation, " +
                     "COALESCE(a.nom, l.nom, u.mail) AS nomInterlocuteur " + // On prend le nom dispo, sinon le mail
                     "FROM DISCUSSION d " +
                     // On joint l'utilisateur qui N'EST PAS moi
                     "JOIN UTILISATEUR u ON (u.idUtilisateur = d.idUtilisateur1 OR u.idUtilisateur = d.idUtilisateur2) " +
                     // On essaie de voir si cet utilisateur est un AGENT
                     "LEFT JOIN AGENT a ON u.idUtilisateur = a.idUtilisateur " +
                     // On essaie de voir si cet utilisateur est un LOUEUR
                     "LEFT JOIN LOUEUR l ON u.idUtilisateur = l.idUtilisateur " +
                     
                     "WHERE (d.idUtilisateur1 = ? OR d.idUtilisateur2 = ?) " +
                     "AND u.idUtilisateur != ?"; 
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Discussion.class), monId, monId, monId);
    }

    // 2. Récupérer les messages d'une discussion
    public List<Message> trouverMessages(int idDiscussion) {
        String sql = "SELECT * FROM MESSAGE WHERE idDiscussion = ? ORDER BY dateHeure ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Message.class), idDiscussion);
    }

    // 3. Envoyer un message
    public void envoyerMessage(Message msg) {
        String sql = "INSERT INTO MESSAGE (idDiscussion, idExpediteur, contenu, dateHeure) VALUES (?, ?, ?, NOW())";
        jdbcTemplate.update(sql, msg.getIdDiscussion(), msg.getIdExpediteur(), msg.getContenu());
    }
}