package com.smartcomplaint.complaintsystem.service;

import com.smartcomplaint.complaintsystem.model.Complaint;
import com.smartcomplaint.complaintsystem.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintRepository repo;
    private final EmailService emailService;

    public ComplaintService(ComplaintRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    public Complaint saveComplaint(Complaint c) {
        if (c.getStatus() == null) c.setStatus("Pending");
        Complaint saved = repo.save(c);

        // send email asynchronously (fire-and-forget)
        new Thread(() -> emailService.sendComplaintReceived(
                saved.getEmail(), saved.getName(), saved.getId(), saved.getDepartment(), saved.getMessage()
        )).start();

        return saved;
    }

    public List<Complaint> getAll() { return repo.findAll(); }

    public Optional<Complaint> getById(Long id) { return repo.findById(id); }

    public List<Complaint> getByStatus(String status) { return repo.findByStatus(status); }

    public Complaint updateStatus(Long id, String status) {
        return repo.findById(id).map(c -> {
            c.setStatus(status);
            return repo.save(c);
        }).orElse(null);
    }

    public void delete(Long id) { repo.deleteById(id); }
}
