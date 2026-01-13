package com.delorent.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.delorent.model.EntrepriseEntretien;

@Repository
public class EntrepriseRepository {

    private final JdbcTemplate jdbc;

    public EntrepriseRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Retourne l'entreprise si email+mdp OK (et si c'est bien une entreprise), sinon null */
    public EntrepriseEntretien findEntrepriseByCredentials(String email, String motDePasse) {

        String sql = """
            SELECT 
                u.idUtilisateur,
                '' AS adresse,
                u.ville,
                u.codePostal,
                u.region,
                u.telephone,
                u.mail,
                u.mdp,
                e.nomEntreprise,
                e.raisonSoc,
                e.noSiret
            FROM UTILISATEUR u
            JOIN ENTREPRISE_ENTRETIEN e ON e.idUtilisateur = u.idUtilisateur
            WHERE u.mail = ? AND u.mdp = ?
        """;

        List<EntrepriseEntretien> list = jdbc.query(sql, (rs, rowNum) ->
                new EntrepriseEntretien(
                        rs.getInt("idUtilisateur"),
                        rs.getString("adresse"),
                        rs.getString("ville"),
                        rs.getString("codePostal"),
                        rs.getString("region"),
                        rs.getString("telephone"),
                        rs.getString("mail"),
                        rs.getString("mdp"),
                        rs.getString("nomEntreprise"),
                        rs.getString("raisonSoc"),
                        rs.getString("noSiret")
                ), email, motDePasse);

        return list.isEmpty() ? null : list.get(0);
    }

    /** Recharge le profil entreprise depuis l'ID (utile pour /profil) */
    public EntrepriseEntretien findEntrepriseById(int idUtilisateur) {

        String sql = """
            SELECT 
                u.idUtilisateur,
                '' AS adresse,
                u.ville,
                u.codePostal,
                u.region,
                u.telephone,
                u.mail,
                u.mdp,
                e.nomEntreprise,
                e.raisonSoc,
                e.noSiret
            FROM UTILISATEUR u
            JOIN ENTREPRISE_ENTRETIEN e ON e.idUtilisateur = u.idUtilisateur
            WHERE u.idUtilisateur = ?
        """;

        List<EntrepriseEntretien> list = jdbc.query(sql, (rs, rowNum) ->
                new EntrepriseEntretien(
                        rs.getInt("idUtilisateur"),
                        rs.getString("adresse"),
                        rs.getString("ville"),
                        rs.getString("codePostal"),
                        rs.getString("region"),
                        rs.getString("telephone"),
                        rs.getString("mail"),
                        rs.getString("mdp"),
                        rs.getString("nomEntreprise"),
                        rs.getString("raisonSoc"),
                        rs.getString("noSiret")
                ), idUtilisateur);

        return list.isEmpty() ? null : list.get(0);
    }
}
