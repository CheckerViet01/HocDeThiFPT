package com.example.websome.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    @GetMapping("/")
    public RedirectView home() {
        // Vào thẳng giao diện web Angular trên cổng 4200
        return new RedirectView("http://localhost:4200");
    }
}
