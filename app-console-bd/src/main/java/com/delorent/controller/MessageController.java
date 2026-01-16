package com.delorent.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.delorent.model.Utilisateur.Utilisateur;
import com.delorent.model.Discussion;
import com.delorent.model.Message;
import com.delorent.repository.MessageRepository;
import com.delorent.service.ConnexionService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MessageController {

    private final MessageRepository messageRepository;
    private final ConnexionService connexionService;

    public MessageController(MessageRepository messageRepository, ConnexionService connexionService) {
        this.messageRepository = messageRepository;
        this.connexionService = connexionService;
    }

    @GetMapping("/messagerie")
    public String afficherMessagerie(@RequestParam(required = false) Integer idDiscussion, 
                                     Model model) {
        
        Utilisateur user = connexionService.getConnexion();
        
        if (user == null) {
            return "redirect:/connexion";
        }

        Integer idConnecte = user.getIdUtilisateur();

        List<Discussion> mesDiscussions = messageRepository.trouverDiscussionsUtilisateur(idConnecte);
        model.addAttribute("discussions", mesDiscussions);
        model.addAttribute("userId", idConnecte);

        if (idDiscussion != null) {
            List<Message> messages = messageRepository.trouverMessages(idDiscussion);
            model.addAttribute("messages", messages);
            model.addAttribute("discussionActive", idDiscussion);
        }

        return "messagerie";
    }

    @PostMapping("/messagerie/envoyer")
    public String envoyer(@RequestParam int idDiscussion, 
                          @RequestParam String contenu) {
        
        Utilisateur user = connexionService.getConnexion();
        if (user == null) return "redirect:/connexion";

        Message msg = new Message();
        msg.setIdDiscussion(idDiscussion);
        msg.setIdExpediteur(user.getIdUtilisateur());
        msg.setContenu(contenu);
        
        messageRepository.envoyerMessage(msg);
        
        return "redirect:/messagerie?idDiscussion=" + idDiscussion;
    }

    @PostMapping("/messagerie/nouveau")
    public String demarrerDiscussion(@RequestParam int idDestinataire) {
        
        Utilisateur user = connexionService.getConnexion();
        
        if (user == null) {
            System.out.println("❌ Redirection : ConnexionService renvoie null");
            return "redirect:/connexion";
        }

        Integer idConnecte = user.getIdUtilisateur();
        System.out.println("✅ Utilisateur identifié ID : " + idConnecte);

        Optional<Discussion> existing = messageRepository.findByUtilisateurs(idConnecte, idDestinataire);

        int idDiscussion;

        if (existing.isPresent()) {
            idDiscussion = existing.get().getIdDiscussion();
        } else {
            Discussion nouvelle = new Discussion();
            nouvelle.setIdUtilisateur1(idConnecte);
            nouvelle.setIdUtilisateur2(idDestinataire);
            nouvelle.setDateCreation(LocalDate.now().toString());
            
            Discussion saved = messageRepository.save(nouvelle);
            idDiscussion = saved.getIdDiscussion();
        }

        return "redirect:/messagerie?idDiscussion=" + idDiscussion;
    }
}