package com.delorent;

import com.delorent.controller.LouerController;
import com.delorent.model.Contrat;
import com.delorent.service.ServiceLocation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;

@SpringBootApplication
public class TestLocationConsole {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(TestLocationConsole.class, args);

        try {
            ServiceLocation serviceLocation = ctx.getBean(ServiceLocation.class);
            LouerController louerController = new LouerController(serviceLocation);

            Contrat contrat = louerController.louerVehicule(
                    1, // idLoueur
                    2, // idLouable (choisis un véhicule dispo)
                    1, // idAssurance (doit être possédée par l'agent du véhicule)
                    LocalDate.of(2026, 1, 20),
                    LocalDate.of(2026, 1, 22),
                    "Toulouse Aéroport" // lieuDepotOptionnel (modifiable)
            );

            System.out.println("Location OK : " + contrat);

        } finally {
            SpringApplication.exit(ctx);
        }
    }
}