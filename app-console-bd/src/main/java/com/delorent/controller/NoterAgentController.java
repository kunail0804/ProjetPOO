package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NoterAgentController {
    public NoterAgentController(){

    }

    @GetMapping("/profilAgent/{id}/noter")
    public String noterAgent(@RequestParam("idAgent") int idAgent, Model model){
        return "noter_agent";
    }
}
