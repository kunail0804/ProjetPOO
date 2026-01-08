package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Donnée de test (optionnelle)
        model.addAttribute("message", "Application Spring Boot OK");
        return "home"; // correspond à templates/home.html
    }
}
