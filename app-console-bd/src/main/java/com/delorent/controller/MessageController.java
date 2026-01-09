package com.delorent.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.delorent.model.Discussion;
import com.delorent.model.Message;
import com.delorent.repository.MessageRepository;

@Controller
public class MessageController {

    private final MessageRepository messageRepository;
    private final int ID_USER_CONNECTE = 1; // Simulation : Je suis Jean

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/messagerie")
    public String afficherMessagerie(@RequestParam(required = false) Integer idDiscussion, Model model) {
        // 1. Charger la liste de mes conversations (Colonne de gauche)
        List<Discussion> mesDiscussions = messageRepository.trouverDiscussionsUtilisateur(ID_USER_CONNECTE);
        model.addAttribute("discussions", mesDiscussions);
        model.addAttribute("userId", ID_USER_CONNECTE);

        // 2. Si une discussion est sélectionnée, charger les messages (Colonne de droite)
        if (idDiscussion != null) {
            List<Message> messages = messageRepository.trouverMessages(idDiscussion);
            model.addAttribute("messages", messages);
            model.addAttribute("discussionActive", idDiscussion);
        }

        return "messagerie";
    }

    @PostMapping("/messagerie/envoyer")
    public String envoyer(@RequestParam int idDiscussion, @RequestParam String contenu) {
        Message msg = new Message();
        msg.setIdDiscussion(idDiscussion);
        msg.setIdExpediteur(ID_USER_CONNECTE);
        msg.setContenu(contenu);
        
        messageRepository.envoyerMessage(msg);
        
        // On recharge la page sur la discussion en cours
        return "redirect:/messagerie?idDiscussion=" + idDiscussion;
    }
}