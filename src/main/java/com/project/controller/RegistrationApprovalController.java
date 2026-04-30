package com.project.controller;

import com.project.entity.*;
import com.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/registration-requests")
@PreAuthorize("hasRole('ADMIN')")
public class RegistrationApprovalController {
    @Autowired
    private RegistrationRequestRepository registrationRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<RegistrationRequest> getAllRequests() {
        return registrationRequestRepository.findAll();
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        RegistrationRequest req = registrationRequestRepository.findById(id).orElse(null);
        if (req == null || req.isApproved()) return ResponseEntity.badRequest().body("Invalid request");
        req.setApproved(true);
        registrationRequestRepository.save(req);
        if ("STUDENT".equalsIgnoreCase(req.getRole())) {
            Student student = Student.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .rollNumber(req.getRollNumber())
                .build();
            studentRepository.save(student);
            return ResponseEntity.ok("Student registration approved and account created");
        } else if ("TEACHER".equalsIgnoreCase(req.getRole())) {
            Teacher teacher = Teacher.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();
            teacherRepository.save(teacher);
            return ResponseEntity.ok("Teacher registration approved and account created");
        }
        // Only create User for admin
        if ("ADMIN".equalsIgnoreCase(req.getRole())) {
            User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.ADMIN)
                .build();
            userRepository.save(user);
            return ResponseEntity.ok("Admin registration approved and account created");
        }
        return ResponseEntity.badRequest().body("Invalid role");
    }

    @DeleteMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        if (registrationRequestRepository.existsById(id)) {
            registrationRequestRepository.deleteById(id);
            return ResponseEntity.ok("Registration request rejected");
        }
        return ResponseEntity.notFound().build();
    }
}
