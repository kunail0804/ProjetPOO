package com.delorent.repository;

import com.delorent.model.Parrainage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ParrainageRepository {

    private final JdbcTemplate jdbc;

    public ParrainageRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsByFilleul(int idFilleul) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM PARRAINAGE WHERE idFilleul = ?",
                Integer.class, idFilleul
        );
        return count != null && count > 0;
    }

    public int create(int idParrain, int idFilleul) {
        // statut EN_ATTENTE par défaut en DB, sinon set ici
        return jdbc.update(
                "INSERT INTO PARRAINAGE (idParrain, idFilleul, dateCreation, statut) VALUES (?, ?, ?, 'EN_ATTENTE')",
                idParrain, idFilleul, Date.valueOf(LocalDate.now())
        );
    }

    public Optional<Parrainage> findPendingByFilleul(int idFilleul) {
        List<Parrainage> list = jdbc.query(
                "SELECT * FROM PARRAINAGE WHERE idFilleul = ? AND statut = 'EN_ATTENTE' LIMIT 1",
                (rs, rowNum) -> {
                    Parrainage p = new Parrainage();
                    p.setIdParrainage(rs.getInt("idParrainage"));
                    p.setIdParrain(rs.getInt("idParrain"));
                    p.setIdFilleul(rs.getInt("idFilleul"));
                    p.setDateCreation(rs.getDate("dateCreation").toLocalDate());
                    p.setStatut(rs.getString("statut"));
                    Date dv = rs.getDate("dateValidation");
                    if (dv != null) p.setDateValidation(dv.toLocalDate());
                    return p;
                },
                idFilleul
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public void validate(int idParrainage) {
        jdbc.update(
                "UPDATE PARRAINAGE SET statut='VALIDE', dateValidation=? WHERE idParrainage=?",
                Date.valueOf(LocalDate.now()), idParrainage
        );
    }

    public List<Parrainage> findByParrain(int idParrain) {
        return jdbc.query(
                "SELECT * FROM PARRAINAGE WHERE idParrain = ? ORDER BY dateCreation DESC",
                (rs, rowNum) -> {
                    Parrainage p = new Parrainage();
                    p.setIdParrainage(rs.getInt("idParrainage"));
                    p.setIdParrain(rs.getInt("idParrain"));
                    p.setIdFilleul(rs.getInt("idFilleul"));
                    p.setDateCreation(rs.getDate("dateCreation").toLocalDate());
                    p.setStatut(rs.getString("statut"));
                    Date dv = rs.getDate("dateValidation");
                    if (dv != null) p.setDateValidation(dv.toLocalDate());
                    return p;
                },
                idParrain
        );
    }
}
