package com.delorent.model.Louable;

import java.util.List;

public class FiltreParUniquementDispo extends AbstractLouableFiltre<Boolean> {
    public FiltreParUniquementDispo(boolean uniquementDisponibles) {
        super("uniquementDisponible",uniquementDisponibles);
    }

    @Override
    public boolean isActif() {
        return valeur() != null && valeur();
    }

    @Override
    public SqlClause toSqlClause() {
        if (valeur() == null) {
            return new SqlClause("", List.of());
        }
        return new SqlClause("l.statut = 'DISPONIBLE'", List.of(valeur()));
    }
}
