package com.project.controller;

import com.project.entity.Teacher;
import com.project.repository.TeacherRepository;
import com.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class TeacherController {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Teacher teacher) {
        if (userRepository.findByEmail(teacher.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Duplicate email");
        }
        teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
        return ResponseEntity.ok(teacherRepository.save(teacher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        return teacherRepository.findById(id)
                .map(t -> {
                    t.setName(teacher.getName());
                    t.setEmail(teacher.getEmail());
                    t.setPassword(passwordEncoder.encode(teacher.getPassword()));
                    return ResponseEntity.ok(teacherRepository.save(t));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (teacherRepository.existsById(id)) {
            teacherRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
