package com.project.service;

import com.project.entity.User;
import com.project.entity.Teacher;
import com.project.entity.Student;
import com.project.repository.UserRepository;
import com.project.repository.TeacherRepository;
import com.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // ✅ CHECK ADMIN USERS
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        }
        
        // ✅ CHECK TEACHERS
        var teacherOpt = teacherRepository.findByEmail(email);
        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            return org.springframework.security.core.userdetails.User.withUsername(teacher.getEmail())
                .password(teacher.getPassword())
                .roles("TEACHER")
                .build();
        }
        
        // ✅ CHECK STUDENTS
        var studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            return org.springframework.security.core.userdetails.User.withUsername(student.getEmail())
                .password(student.getPassword())
                .roles("STUDENT")
                .build();
        }
        
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
