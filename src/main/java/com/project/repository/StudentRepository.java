package com.project.repository;

import com.project.entity.Student;
import com.project.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    Optional<User> findByRollNumber(String rollNumber);
    
    List<Student> findBySectionId(Long sectionId);
}