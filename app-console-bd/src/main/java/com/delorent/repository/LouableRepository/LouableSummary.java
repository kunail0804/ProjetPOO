package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.StatutLouable;

public record LouableSummary(
        int idLouable,
        StatutLouable statut,
        double prixJour,
        String lieuPrincipal,
        String type
) {
    // Constructeur compact (PAS de this(...) ici)
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
}