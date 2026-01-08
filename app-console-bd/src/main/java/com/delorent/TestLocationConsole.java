package com.delorent;

import com.delorent.controller.LouerController;
import com.delorent.model.Contrat;
import com.delorent.service.ServiceLocation;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;

public class TestLocationConsole {

    public static void main(String[] args) {
        ConfigurableApplicationContext contexte = SpringApplication.run(App.class, args);

        // Récupère ton service réel (implémentation @Service)
        ServiceLocation serviceLocation = contexte.getBean(ServiceLocation.class);

        // Ton contrôleur n’est pas un bean Spring -> on l’instancie
        LouerController controleur = new LouerController(serviceLocation);

        int idLoueur = 1;
        int idLouable = 1;

        // Mets ici un id_assurance qui existe (SELECT * FROM ASSURANCE;)
        int idAssurance = 1;

        LocalDate dateDebut = LocalDate.now().plusDays(1);
        LocalDate dateFin = LocalDate.now().plusDays(4);

        // null si pas d’option dépôt différent, sinon un lieu
        String lieuDepotOptionnel = "Toulouse";

        Contrat contrat = controleur.louerVehicule(
                idLoueur, idLouable, idAssurance, dateDebut, dateFin, lieuDepotOptionnel
        );

        System.out.println("Location OK : " + contrat);

        contexte.close();
    }
}