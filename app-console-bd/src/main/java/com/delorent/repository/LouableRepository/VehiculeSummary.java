package com.delorent.repository.LouableRepository;

public record VehiculeSummary(
        LouableSummary louable,
        String marque,
        String modele,
        int annee,
        String couleur,
        String immatriculation,
        int kilometrage,
        String type
) {}