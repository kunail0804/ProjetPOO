package com.delorent.repository;
import com.delorent.model.StatutLouable;
import com.delorent.model.Vehicule;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

@Repository
public interface VehiculeRepository 
{

    //liste de tous les vehicules disponibles aujourd'hui//
    
     List<Vehicule> findByDisponibleTrue();
    
   //liste des vehicules disponibles par lieu (ville)//

    List<Vehicule> findByLieuPrincipal(String lieu);

   //recupère la liste de vehicule par statut (disponible, indisponible, en location...)//

    List<Vehicule> findByStatut(StatutLouable statut);

   //affiche les vehicules disponibles avec la note globale supérieure ou égale à une valeur//
   
    List<Vehicule> findByNoteGlobaleGreaterThanEqual(Double note);

    //recupère les vehicules disponibles par type (camion, moto...)//
     List<Vehicule> findByType(String type);

    //verifie si un vehicule est disponible avec la date de debut et fin//
     boolean existsByStatutAndDateDebutDisponibiliteBeforeAndDateFinDisponibiliteAfter(
    StatutLouable statut, 
    LocalDate dateFin, 
    LocalDate dateDebut
);
   //compte le nombre de vehicules disponibles//
   
Long countVehiculesDisponibles();

     }

    