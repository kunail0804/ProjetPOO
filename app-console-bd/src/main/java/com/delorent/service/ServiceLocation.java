package com.delorent.service;

import com.delorent.controller.ContratLocation;
import com.delorent.dto.DemandeLocation;

public interface ServiceLocation {
    ContratLocation louer(DemandeLocation demande);
}