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

    // 1. Trouver toutes mes discussions ET le nom de la personne avec qui je parle
    public List<Discussion> trouverDiscussionsUtilisateur(int monId) {
        // Cette requête regarde si je suis user1 OU user2
        // Et elle joint la table UTILISATEUR pour récupérer le nom de celui qui N'EST PAS moi
        String sql = "SELECT d.idDiscussion, d.dateCreation, " +
                     "u.nom AS nomInterlocuteur " + // On met le nom de l'autre dans l'objet Discussion
                     "FROM DISCUSSION d " +
                     "JOIN UTILISATEUR u ON (u.idUtilisateur = d.idUtilisateur1 OR u.idUtilisateur = d.idUtilisateur2) " +
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