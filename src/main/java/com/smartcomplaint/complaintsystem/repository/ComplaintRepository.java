package com.smartcomplaint.complaintsystem.repository;

import com.smartcomplaint.complaintsystem.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStatus(String status);
    List<Complaint> findByEmail(String email);

}
