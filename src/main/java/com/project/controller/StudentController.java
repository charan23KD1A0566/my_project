package com.project.controller;

import com.project.entity.Student;
import com.project.repository.StudentRepository;
import com.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/students")
@PreAuthorize("hasRole('ADMIN')")
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Student student) {
        if (userRepository.findByEmail(student.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Duplicate email");
        }
        if (studentRepository.findByRollNumber(student.getRollNumber()).isPresent()) {
            return ResponseEntity.badRequest().body("Duplicate roll number");
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student student) {
        return studentRepository.findById(id)
                .map(s -> {
                    s.setName(student.getName());
                    s.setEmail(student.getEmail());
                    s.setRollNumber(student.getRollNumber());
                    s.setPassword(passwordEncoder.encode(student.getPassword()));
                    return ResponseEntity.ok(studentRepository.save(s));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
