package com.delorent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CrudVehiculeController {
     @GetMapping("/crudVehicule")
    public String crudVehicule() {
        return "crudVehicule";
    }
}