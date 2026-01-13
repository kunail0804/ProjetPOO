package com.delorent.model.Louable;

public enum StatutLouable {
    DISPONIBLE,
    EN_LOCATION,
    INDISPONIBLE;

    public static StatutLouable fromDb(String raw) {
        if (raw == null) return INDISPONIBLE;

        String s = raw.trim().toUpperCase()
                .replace(' ', '_')
                .replace('-', '_');

        if (s.equals("ENLOCATION")) s = "EN_LOCATION";
        if (s.equals("DISPO")) s = "DISPONIBLE";

        try {
            return StatutLouable.valueOf(s);
        } catch (IllegalArgumentException e) {
            return INDISPONIBLE;
        }
    }
}