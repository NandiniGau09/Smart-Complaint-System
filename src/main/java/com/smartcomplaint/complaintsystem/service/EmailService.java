package com.smartcomplaint.complaintsystem.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendEmail(String to, String subject, String htmlBody) {
        try {
            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + resendApiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString =
                    "{"
                            + "\"from\":\"ComplaintSystem <noreply@resend.dev>\","
                            + "\"to\":\"" + to + "\","
                            + "\"subject\":\"" + subject + "\","
                            + "\"html\":\"" + htmlBody.replace("\"", "\\\"") + "\""
                            + "}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            System.out.println("Resend API Response: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** EMAIL WHEN COMPLAINT IS CREATED **/
    public void sendComplaintReceived(
            String name,
            String email,
            Long complaintId,
            String subject,
            String message) {

        String body =
                "<p>Dear <strong>" + name + "</strong>,</p>"
                        + "<p>Your complaint has been successfully registered.</p>"
                        + "<p><strong>Complaint ID:</strong> " + complaintId + "<br>"
                        + "<strong>Subject:</strong> " + subject + "<br>"
                        + "<strong>Message:</strong> " + message + "</p>"
                        + "<p>We will get back to you soon.<br>Thank you.</p>";

        sendEmail(email, "Complaint Received - ID " + complaintId, body);
    }

    /** EMAIL WHEN STATUS IS UPDATED **/
    public void sendStatusUpdated(
            String name,
            String email,
            Long complaintId,
            String newStatus) {

        String body =
                "<p>Hello <strong>" + name + "</strong>,</p>"
                        + "<p>The status of your complaint has been updated.</p>"
                        + "<p><strong>Complaint ID:</strong> " + complaintId + "<br>"
                        + "<strong>New Status:</strong> " + newStatus + "</p>"
                        + "<p>We will notify you of further updates.<br>Thank you.</p>";

        sendEmail(email, "Complaint Status Updated - ID " + complaintId, body);
    }
}
