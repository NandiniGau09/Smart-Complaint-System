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
                "<div style='font-family:Arial;background:#f4f4f4;padding:20px;'>"
                        + "<div style='background:white;padding:20px;border-radius:8px;border:1px solid #ddd;'>"
                        + "<h2 style='color:#0066cc;text-align:center;'>Your Complaint Has Been Received</h2>"
                        + "<p>Hello <strong>" + name + "</strong>,</p>"
                        + "<p>Your complaint has been successfully submitted.</p>"
                        + "<hr>"
                        + "<p><strong style='color:#000;'>Complaint ID:</strong> <span style='color:#d9534f'><strong>" + complaintId + "</strong></span></p>"
                        + "<p><strong>Department:</strong> " + subject + "</p>"
                        + "<p><strong>Message:</strong><br>" + message + "</p>"
                        + "<hr>"
                        + "<p style='font-size:13px;color:#777;text-align:center;'>"
                        + "Thank you for reaching out.<br>"
                        + "Complaint Support Team<br>"
                        + "Smart Complaint System"
                        + "</p>"
                        + "</div></div>";

        sendEmail(email, "Complaint Received - ID " + complaintId, body);
    }

    /** EMAIL WHEN STATUS IS UPDATED **/
    public void sendStatusUpdated(
            String name,
            String email,
            Long complaintId,
            String newStatus) {

        String body =
                "<div style='font-family:Arial;background:#f4f4f4;padding:20px;'>"
                        + "<div style='background:white;padding:20px;border-radius:8px;border:1px solid #ddd;'>"
                        + "<h2 style='color:#0066cc;text-align:center;'>Complaint Status Update</h2>"
                        + "<p>Hello <strong>" + name + "</strong>,</p>"
                        + "<p>Your complaint status has been updated.</p>"
                        + "<hr>"
                        + "<p><strong>Complaint ID:</strong> <span style='color:#d9534f'><strong>" + complaintId + "</strong></span></p>"
                        + "<p><strong>New Status:</strong> " + newStatus + "</p>"
                        + "<hr>"
                        + "<p style='font-size:13px;color:#777;text-align:center;'>"
                        + "We will notify you of further updates.<br>"
                        + "Thank you.<br>"
                        + "Complaint Support Team<br>"
                        + "Smart Complaint System"
                        + "</p>"
                        + "</div></div>";

        sendEmail(email, "Complaint Status Updated - ID " + complaintId, body);
    }
}
