package com.project.repository;

import com.project.entity.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, Long> {
    boolean existsByEmail(String email);
    boolean existsByRollNumber(String rollNumber);
}
