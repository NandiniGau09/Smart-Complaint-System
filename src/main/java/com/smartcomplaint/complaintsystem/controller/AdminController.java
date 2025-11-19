package com.smartcomplaint.complaintsystem.controller;

import com.smartcomplaint.complaintsystem.model.AdminLogin;
import com.smartcomplaint.complaintsystem.model.Complaint;
import com.smartcomplaint.complaintsystem.repository.ComplaintRepository;
import com.smartcomplaint.complaintsystem.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private EmailService emailService;

    // -------------------------
    // SHOW LOGIN PAGE
    // -------------------------
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("adminLogin", new AdminLogin());
        return "admin/login";
    }

    // -------------------------
    // PROCESS LOGIN
    // -------------------------
    @PostMapping("/login")
    public String login(@ModelAttribute("adminLogin") AdminLogin adminLogin,
            Model model,
            HttpSession session) {

        String USER = "admin";
        String PASS = "admin123";

        if (adminLogin.getUsername().equals(USER)
                && adminLogin.getPassword().equals(PASS)) {

            session.setAttribute("adminLogged", true);
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid username or password!");
        return "admin/login";
    }

    // -------------------------
    // DASHBOARD
    // -------------------------
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (session.getAttribute("adminLogged") == null) {
            return "redirect:/admin/login";
        }
        return "admin/dashboard";
    }

    // -------------------------
    // LIST COMPLAINTS
    // -------------------------
    @GetMapping("/complaints")
    public String complaintsList(Model model, HttpSession session) {
        if (session.getAttribute("adminLogged") == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("complaints", complaintRepository.findAll());
        return "admin/list";
    }

    // -------------------------
    // VIEW COMPLAINT
    // -------------------------
    @GetMapping("/complaints/{id}")
    public String viewComplaint(@PathVariable Long id, Model model, HttpSession session) {

        if (session.getAttribute("adminLogged") == null) {
            return "redirect:/admin/login";
        }

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Complaint ID: " + id));

        model.addAttribute("complaint", complaint);
        return "admin/details";
    }

    // -------------------------
    // UPDATE STATUS + SEND EMAIL IF RESOLVED
    // -------------------------
    @PostMapping("/complaints/update-status/{id}")
    public String updateStatus(@PathVariable Long id,
            @RequestParam("status") String status,
            HttpSession session) {

        if (session.getAttribute("adminLogged") == null) {
            return "redirect:/admin/login";
        }

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Complaint ID: " + id));

        complaint.setStatus(status);
        complaintRepository.save(complaint);

        // -------------------------
        // SEND EMAIL IF RESOLVED
        // -------------------------
        if (status.equals("Resolved")) {
            String subject = "Your Complaint Has Been Resolved";
            String body = "Dear " + complaint.getName() + ",\n\n"
                    + "Your complaint has been successfully resolved.\n\n"
                    + "Complaint ID: " + complaint.getId() + "\n"
                    + "Message: " + complaint.getMessage() + "\n\n"
                    + "Thank you for using the Smart Complaint System!\n";

            emailService.sendEmail(complaint.getEmail(), subject, body);
        }

        return "redirect:/admin/complaints?updated=true";
    }

    // -------------------------
    // DELETE COMPLAINT
    // -------------------------
    @GetMapping("/complaints/delete/{id}")
    public String deleteComplaint(@PathVariable Long id, HttpSession session) {

        if (session.getAttribute("adminLogged") == null) {
            return "redirect:/admin/login";
        }

        complaintRepository.deleteById(id);
        return "redirect:/admin/complaints?deleted=true";
    }

    // -------------------------
    // LOGOUT
    // -------------------------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
