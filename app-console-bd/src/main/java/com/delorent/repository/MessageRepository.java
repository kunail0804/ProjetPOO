package com.delorent.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.delorent.model.Discussion;
import com.delorent.model.Message;

@Repository
public class MessageRepository {

    private final JdbcTemplate jdbcTemplate;

    public MessageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Discussion> trouverDiscussionsUtilisateur(int monId) {
        String sql = "SELECT " +
                     "d.idDiscussion, " +
                     "d.dateCreation, " +
                     "COALESCE(a.nom, l.nom, u.mail) AS nomInterlocuteur " +
                     "FROM DISCUSSION d " +
                     "JOIN UTILISATEUR u ON (u.idUtilisateur = d.idUtilisateur1 OR u.idUtilisateur = d.idUtilisateur2) " +
                     "LEFT JOIN AGENT a ON u.idUtilisateur = a.idUtilisateur " +
                     "LEFT JOIN LOUEUR l ON u.idUtilisateur = l.idUtilisateur " +
                     "WHERE (d.idUtilisateur1 = ? OR d.idUtilisateur2 = ?) " +
                     "AND u.idUtilisateur != ?"; 
        
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Discussion.class), monId, monId, monId);
    }

    public List<Message> trouverMessages(int idDiscussion) {
        String sql = "SELECT * FROM MESSAGE WHERE idDiscussion = ? ORDER BY dateHeure ASC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Message.class), idDiscussion);
    }

    public void envoyerMessage(Message msg) {
        String sql = "INSERT INTO MESSAGE (idDiscussion, idExpediteur, contenu, dateHeure) VALUES (?, ?, ?, NOW())";
        jdbcTemplate.update(sql, msg.getIdDiscussion(), msg.getIdExpediteur(), msg.getContenu());
    }

    public Optional<Discussion> findByUtilisateurs(int idUser1, int idUser2) {
        String sql = "SELECT * FROM DISCUSSION " + 
                     "WHERE (idUtilisateur1 = ? AND idUtilisateur2 = ?) " +
                     "   OR (idUtilisateur1 = ? AND idUtilisateur2 = ?)";

        try {
            List<Discussion> resultats = jdbcTemplate.query(sql, 
                new Object[]{idUser1, idUser2, idUser2, idUser1},
                (rs, rowNum) -> {
                    Discussion discussion = new Discussion();
                    discussion.setIdDiscussion(rs.getInt("idDiscussion"));
                    discussion.setIdUtilisateur1(rs.getInt("idUtilisateur1"));
                    discussion.setIdUtilisateur2(rs.getInt("idUtilisateur2"));
                    discussion.setDateCreation(rs.getString("dateCreation"));
                    return discussion;
                }
            );

            if (resultats.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(resultats.get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Discussion save(Discussion discussion) {
        String sql = "INSERT INTO DISCUSSION (idUtilisateur1, idUtilisateur2, dateCreation) VALUES (?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, discussion.getIdUtilisateur1());
            ps.setInt(2, discussion.getIdUtilisateur2());
            ps.setString(3, discussion.getDateCreation());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            discussion.setIdDiscussion(keyHolder.getKey().intValue());
        }

        return discussion;
    }
}