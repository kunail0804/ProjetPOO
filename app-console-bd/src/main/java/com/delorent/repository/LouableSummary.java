package com.delorent.repository;

import com.delorent.model.StatutLouable;

public record LouableSummary(
    int idLouable,
    double prixJour,
    StatutLouable statut,
    String lieuPrincipal
) {}