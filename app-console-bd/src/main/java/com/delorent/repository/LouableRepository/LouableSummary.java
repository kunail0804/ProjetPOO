package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.StatutLouable;

public record LouableSummary(
        int idLouable,
        int idAgent,
        StatutLouable statut,
        double prixJour,
        String lieuPrincipal,
        String type,
        boolean disponibleAujourdhui
) {

    public LouableSummary {
        if (statut == null) {
            statut = StatutLouable.INDISPONIBLE;
        }
        if (lieuPrincipal == null) {
            lieuPrincipal = "";
        }
        if (type == null || type.isBlank()) {
            type = "Louable";
        }
    }

    public LouableSummary(int idLouable, StatutLouable statut, double prixJour, String lieuPrincipal, String type) {
        this(idLouable, 0, statut, prixJour, lieuPrincipal, type, statut == StatutLouable.DISPONIBLE);
    }
}