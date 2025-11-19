package com.smartcomplaint.complaintsystem.controller;

import com.smartcomplaint.complaintsystem.model.Complaint;
import com.smartcomplaint.complaintsystem.repository.ComplaintRepository;
import com.smartcomplaint.complaintsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private EmailService emailService;

    // Create new complaint form
    @GetMapping("/new")
    public String newComplaintForm(Model model) {
        model.addAttribute("complaint", new Complaint());
        return "complaint";
    }

    // Submit complaint
    @PostMapping("/submit")
    public String submitComplaint(@ModelAttribute Complaint complaint, Model model) {

        complaintRepository.save(complaint);

        // Send email to user
        try {
            String subject = "Your Complaint Has Been Received";
            String message =
                    "Hello " + complaint.getName() + ",\n\n" +
                    "Your complaint has been successfully submitted.\n\n" +
                    "Complaint Details:\n" +
                    "------------------------------\n" +
                    "Name: " + complaint.getName() + "\n" +
                    "Email: " + complaint.getEmail() + "\n" +
                    "Message: " + complaint.getMessage() + "\n\n" +
                    "Thank you for reaching out.\n\n" +
                    "Regards,\nComplaint Support Team";

            emailService.sendEmail(complaint.getEmail(), subject, message);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }

        model.addAttribute("message", "Complaint submitted successfully!");
        return "success";
    }

    // List all complaints
    @GetMapping("/list")
    public String listComplaints(Model model) {
        model.addAttribute("complaints", complaintRepository.findAll());
        return "complaints_list";
    }

    // View single complaint
    @GetMapping("/{id}")
    public String viewComplaint(@PathVariable Long id, Model model) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid complaint ID: " + id));

        model.addAttribute("complaint", complaint);
        return "view_complaint";
    }

    // Delete complaint + email
    @GetMapping("/delete/{id}")
    public String deleteComplaint(@PathVariable Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElse(null);

        if (complaint != null) {
            // Send delete notification email
            try {
                String subject = "Your Complaint Has Been Removed";
                String message =
                        "Hello " + complaint.getName() + ",\n\n" +
                        "Your complaint has been deleted by the admin.\n\n" +
                        "Regards,\nComplaint Support Team";

                emailService.sendEmail(complaint.getEmail(), subject, message);

            } catch (Exception e) {
                System.out.println("Error sending delete email: " + e.getMessage());
            }

            // Delete from DB
            complaintRepository.deleteById(id);
        }

        return "redirect:/complaints/list";
    }
}
