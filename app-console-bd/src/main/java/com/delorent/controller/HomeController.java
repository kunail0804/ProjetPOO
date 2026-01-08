package com.delorent.controller;

import com.delorent.repository.LocationRepository;
import com.delorent.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    public HomeController(UserRepository userRepository, LocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Application Spring Boot OK");

        try {
            long count = userRepository.countUsers();
            List<Map<String, Object>> users = userRepository.findAllUsersRaw();

            model.addAttribute("dbOk", true);
            model.addAttribute("dbCount", count);
            model.addAttribute("users", users);

            model.addAttribute("loueurs", locationRepository.listerLoueurs());
            model.addAttribute("louables", locationRepository.listerLouables());
            model.addAttribute("assurances", locationRepository.listerAssurances());
            model.addAttribute("contrats", locationRepository.listerContrats());

        } catch (Exception e) {
            model.addAttribute("dbOk", false);
            model.addAttribute("dbError", e.getClass().getSimpleName() + ": " + e.getMessage());
            model.addAttribute("dbCount", null);
            model.addAttribute("users", Collections.emptyList());

            model.addAttribute("loueurs", Collections.emptyList());
            model.addAttribute("louables", Collections.emptyList());
            model.addAttribute("assurances", Collections.emptyList());
            model.addAttribute("contrats", Collections.emptyList());
        }

        return "home";
    }
}