package com.delorent.repository.LouableRepository;

import com.delorent.model.Louable.StatutLouable;

public record LouableSummary(
        // On garde TOUS les champs (maximum d'infos)
        int idLouable,
        int idAgent,           // Nécessaire pour HEAD
        StatutLouable statut,
        double prixJour,
        String lieuPrincipal,
        String type,
        boolean disponibleAujourdhui // Nécessaire pour HEAD
) {

    // 1. Validation automatique (Apport de US.L.10)
    // S'exécute à chaque création d'objet
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


    // 2. Constructeur "Compatibilité US.L.10"
    // Permet au code de tes collègues de créer un résumé sans connaître l'agent ou la dispo précise.
    // On met 0 pour l'agent et on déduit la dispo du statut global.
    public LouableSummary(int idLouable, StatutLouable statut, double prixJour, String lieuPrincipal, String type) {
        this(idLouable, 0, statut, prixJour, lieuPrincipal, type, statut == StatutLouable.DISPONIBLE);
    }
}