package com.delorent.model.Louable;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Filtre : ne garder que les louables ayant au moins un créneau DISPONIBILITE
 * couvrant une date donnée.
 *
 * Hypothèses SQL :
 * - table DISPONIBILITE(idLouable, dateDebut, dateFin)
 * - table LOUABLE alias "l" avec colonne id
 */
public final class FiltreDateDisponible extends AbstractLouableFiltre<LocalDate> {

    public FiltreDateDisponible(LocalDate date) {
        super("dateDisponible", date);
    }

    @Override
    public boolean isActif() {
        return valeur() != null;
    }

    @Override
    public SqlClause toSqlClause() {
        if (valeur() == null) {
            return new SqlClause("", List.of());
        }

        // IMPORTANT : on passe une java.sql.Date en param, sinon JDBC peut mal binder LocalDate selon config
        Date d = Date.valueOf(valeur());

        String predicate =
                "EXISTS (" +
                "  SELECT 1 FROM DISPONIBILITE dp " +
                "  WHERE dp.idLouable = l.id " +
                "    AND ? BETWEEN dp.dateDebut AND dp.dateFin" +
                ")";

        return new SqlClause(predicate, List.of(d));
    }
}