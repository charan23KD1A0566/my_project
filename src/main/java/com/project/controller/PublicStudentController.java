package com.project.controller;

import com.project.entity.Student;
import com.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class PublicStudentController {
    
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @GetMapping("/department/{departmentId}")
    public List<Student> getByDepartment(@PathVariable Long departmentId) {
        return studentRepository.findAll().stream()
                .filter(s -> s.getSection().getDepartment().getId().equals(departmentId))
                .toList();
    }

    @GetMapping("/section/{sectionId}")
    public List<Student> getBySection(@PathVariable Long sectionId) {
        return studentRepository.findBySectionId(sectionId);
    }
}
