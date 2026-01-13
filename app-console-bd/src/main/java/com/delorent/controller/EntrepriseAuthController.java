package com.delorent.controller;

import com.delorent.model.EntrepriseEntretien;
import com.delorent.repository.EntrepriseRepository;
import com.delorent.service.AuthEntrepriseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EntrepriseAuthController {

    private final AuthEntrepriseService authService;
    private final EntrepriseRepository entrepriseRepo;

    public EntrepriseAuthController(AuthEntrepriseService authService, EntrepriseRepository entrepriseRepo) {
        this.authService = authService;
        this.entrepriseRepo = entrepriseRepo;
    }

    @GetMapping("/entreprise/login")
    public String pageLogin() {
        return "entreprise-login";
    }

    @PostMapping("/entreprise/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String motDePasse,
                          HttpSession session,
                          Model model) {

        EntrepriseEntretien entreprise = authService.login(email, motDePasse);

        if (entreprise == null) {
            model.addAttribute("error", "Identifiants invalides ou compte non entreprise.");
            model.addAttribute("email", email);
            return "entreprise-login";
        }

        session.setAttribute("entrepriseId", entreprise.getIdUtilisateur());
        return "redirect:/entreprise/profil";
    }

    @GetMapping("/entreprise/profil")
    public String profil(HttpSession session, Model model) {

        Integer entrepriseId = (Integer) session.getAttribute("entrepriseId");
        if (entrepriseId == null) {
            return "redirect:/entreprise/login";
        }

        EntrepriseEntretien entreprise = entrepriseRepo.findEntrepriseById(entrepriseId);
        if (entreprise == null) {
            session.invalidate();
            return "redirect:/entreprise/login";
        }

        model.addAttribute("entreprise", entreprise);
        return "entreprise-profil";
    }

    @PostMapping("/entreprise/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/entreprise/login";
    }
}
