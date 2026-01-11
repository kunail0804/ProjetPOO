package com.delorent.model.Louable;

import java.util.List;

public final class FiltrePrixMax extends AbstractLouableFiltre<Double> {

    public FiltrePrixMax(Double prixMax) {
        super("prixMax", prixMax);
    }

    @Override
    public boolean isActif() {
        return valeur() != null && valeur() >= 0;
    }

    @Override
    public SqlClause toSqlClause() {
        if (valeur() == null) {
            return new SqlClause("", List.of());
        }
        return new SqlClause("l.prixJour <= ?", List.of(valeur()));
    }
}
