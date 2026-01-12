package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.StatutLouable;

public record LouableSummary(
    Integer idLouable,
    Integer idAgent,
    StatutLouable statut,
    double prixJour,
    String lieuPrincipal,
    String type
) {}
