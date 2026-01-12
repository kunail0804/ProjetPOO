// FICHIER: src/main/java/com/delorent/repository/LouableRepository/LouableSummary.java
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
) {}