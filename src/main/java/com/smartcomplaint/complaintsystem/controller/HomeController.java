package com.smartcomplaint.complaintsystem.controller;

import com.smartcomplaint.complaintsystem.model.Complaint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("complaint", new Complaint()); // ADD THIS LINE
        return "index";
    }
}
