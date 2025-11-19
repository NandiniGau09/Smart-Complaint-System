package com.smartcomplaint.complaintsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    public void sendComplaintReceived(String name, String email, Long complaintId, String subject, String message) {

        String body = "Dear " + name + ",\n\n"
                + "Your complaint has been successfully registered.\n"
                + "Complaint ID: " + complaintId + "\n"
                + "Subject: " + subject + "\n"
                + "Message: " + message + "\n\n"
                + "We will get back to you soon.\n"
                + "Thank you.";

        sendEmail(email, "Complaint Received - ID " + complaintId, body);
    }
}
