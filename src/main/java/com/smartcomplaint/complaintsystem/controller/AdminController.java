package com.smartcomplaint.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcomplaint.complaintsystem.model.AdminLogin;
import com.smartcomplaint.complaintsystem.model.Complaint;
import com.smartcomplaint.complaintsystem.repository.ComplaintRepository;
import com.smartcomplaint.complaintsystem.service.EmailService;

import jakarta.servlet.http.HttpSession;

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
    // UPDATE STATUS + SEND EMAIL
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

        // SEND EMAIL ALWAYS — not only when resolved
        try {
            emailService.sendStatusUpdated(
                    complaint.getName(),
                    complaint.getEmail(),
                    complaint.getId(),
                    status
            );
            System.out.println("📩 Status update email sent");
        } catch (Exception e) {
            System.out.println("❗ Error sending status update email: " + e.getMessage());
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

        Complaint complaint = complaintRepository.findById(id)
                .orElse(null);

        if (complaint != null) {

            try {
                String subject = "Your Complaint Has Been Removed";

                String body =
                        "<p>Hello <strong>" + complaint.getName() + "</strong>,</p>"
                                + "<p>Your complaint has been deleted by the admin.</p>"
                                + "<p>Regards,<br>Complaint Support Team</p>";

                emailService.sendEmail(complaint.getEmail(), subject, body);
                System.out.println("📩 Delete notification email sent");

            } catch (Exception e) {
                System.out.println("❗ Error sending delete email: " + e.getMessage());
            }

            complaintRepository.deleteById(id);
        }

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
