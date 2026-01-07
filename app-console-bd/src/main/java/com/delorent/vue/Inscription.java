package com.delorent.vue;

import java.util.Scanner;

public class Inscription {

    public Inscription() {
    }

    public void inscrire() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez votre nom: ");
        String nom = scanner.nextLine();

        System.out.print("Entrez votre prénom: ");
        String prenom = scanner.nextLine();

        System.out.print("Entrez votre email: ");
        String email = scanner.nextLine();

        System.out.print("Entrez votre mot de passe: ");
        String motDePasse = scanner.nextLine();

        System.out.println("Inscription réussie pour " + prenom + " " + nom + " avec l'email " + email);

        scanner.close();
    }
}
