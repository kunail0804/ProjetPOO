package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.StatutLouable;

public record LouableSummary(
    Integer idLouable,
    Integer idAgent,
    StatutLouable statut,
    double prixJour,
    String lieuPrincipal,
    String type
) {
    public LouableSummary(Integer idLouable, Integer idAgent, StatutLouable statut, double prixJour, String lieuPrincipal, String type) {
        this.idLouable = idLouable;
        this.idAgent = idAgent;
        this.statut = statut;
        this.prixJour = prixJour;
        this.lieuPrincipal = lieuPrincipal;
        this.type = type;
    }
}
