package com.project.controller;

import com.project.entity.RegistrationRequest;
import com.project.entity.Role;
import com.project.repository.RegistrationRequestRepository;
import com.project.repository.UserRepository;
import com.project.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    @Autowired
    private RegistrationRequestRepository registrationRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================= REGISTER REQUEST =================
    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegistrationRequest req) {

        Map<String, Object> response = new HashMap<>();

        // ================= EMAIL CHECK =================
        boolean emailExists =
                userRepository.findByEmail(req.getEmail()).isPresent()
                || registrationRequestRepository.existsByEmail(req.getEmail());

        if (emailExists) {
            response.put("message", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // ================= STUDENT ROLL CHECK =================
        if (req.getRole() != null &&
                req.getRole().equalsIgnoreCase("STUDENT") &&
                req.getRollNumber() != null) {

            boolean rollExists =
                    studentRepository.findByRollNumber(req.getRollNumber()).isPresent()
                    || registrationRequestRepository.existsByRollNumber(req.getRollNumber());

            if (rollExists) {
                response.put("message", "Roll number already exists");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // ================= PASSWORD ENCODING =================
        req.setPassword(passwordEncoder.encode(req.getPassword()));

        // ================= DEFAULT STATUS =================
        req.setApproved(false);

        // ================= SAVE REQUEST =================
        registrationRequestRepository.save(req);

        response.put("message", "Registration request submitted for approval");
        response.put("status", "PENDING");

        return ResponseEntity.ok(response);
    }
}