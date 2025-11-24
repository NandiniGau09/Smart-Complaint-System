package com.smartcomplaint.complaintsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smartcomplaint.complaintsystem.model.Complaint;
import com.smartcomplaint.complaintsystem.repository.ComplaintRepository;
import com.smartcomplaint.complaintsystem.service.EmailService;

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

        try {
            emailService.sendComplaintReceived(
                    complaint.getName(),
                    complaint.getEmail(),
                    complaint.getId(),
                    complaint.getDepartment(),
                    complaint.getMessage()
            );

            System.out.println("📩 Complaint received email sent");
        } catch (Exception e) {
            System.out.println("❗ Error sending complaint received email: " + e.getMessage());
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

    // Update complaint status + send email
    @PostMapping("/update-status/{id}")
    public String updateComplaintStatus(@PathVariable Long id, @ModelAttribute Complaint updatedComplaint) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid complaint ID: " + id));

        // set new status
        complaint.setStatus(updatedComplaint.getStatus());
        complaintRepository.save(complaint);

        try {
            emailService.sendStatusUpdated(
                    complaint.getName(),
                    complaint.getEmail(),
                    complaint.getId(),
                    complaint.getStatus()
            );

            System.out.println("📩 Status update email sent");

        } catch (Exception e) {
            System.out.println("❗ Error sending status update email: " + e.getMessage());
        }

        return "redirect:/complaints/list";
    }

    // Delete complaint + email
    @GetMapping("/delete/{id}")
    public String deleteComplaint(@PathVariable Long id) {

        Complaint complaint = complaintRepository.findById(id)
                .orElse(null);

        if (complaint != null) {

            try {
                String subject = "Your Complaint Has Been Removed";

                String message =
                        "<p>Hello <strong>" + complaint.getName() + "</strong>,</p>"
                                + "<p>Your complaint has been deleted by the admin.</p>"
                                + "<p>Regards,<br>Complaint Support Team</p>";

                emailService.sendEmail(complaint.getEmail(), subject, message);

            } catch (Exception e) {
                System.out.println("❗ Error sending delete email: " + e.getMessage());
            }

            complaintRepository.deleteById(id);
        }

        return "redirect:/complaints/list";
    }
}
