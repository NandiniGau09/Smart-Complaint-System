package com.smartcomplaint.complaintsystem.controller;

import com.smartcomplaint.complaintsystem.model.Complaint;
import com.smartcomplaint.complaintsystem.repository.ComplaintRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ComplaintRepository complaintRepository;

    // Show only user's own complaints
    @GetMapping("/complaints")
    public String listUserComplaints(Model model, HttpSession session) {

        String email = (String) session.getAttribute("userEmail");

        List<Complaint> complaints = email != null
                ? complaintRepository.findByEmail(email)
                : List.of();

        model.addAttribute("complaints", complaints);

        return "complaints_list";
    }
}
