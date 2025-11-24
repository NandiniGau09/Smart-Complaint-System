package com.smartcomplaint.complaintsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcomplaint.complaintsystem.model.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // ✅ Find complaints by status (ex: Pending, Resolved etc.)
    List<Complaint> findByStatus(String status);

    // ✅ Find complaints by user email
    List<Complaint> findByEmail(String email);
}
