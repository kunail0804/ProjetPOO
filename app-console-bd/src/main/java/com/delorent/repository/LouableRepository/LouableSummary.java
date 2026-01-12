package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.StatutLouable;

public record LouableSummary(
    Integer idLouable,
    StatutLouable statutBase,      // statut stocké en base (DISPONIBLE / EN_LOCATION / etc.)
    double prixJour,
    String lieuPrincipal,
    String type,
    boolean disponibleLeJour        // statut calculé via DISPONIBILITE pour la date sélectionnée
) {}