package com.project.controller;

import com.project.entity.Student;
import com.project.entity.Teacher;
import com.project.entity.User;
import com.project.entity.Role;
import com.project.repository.StudentRepository;
import com.project.repository.TeacherRepository;
import com.project.repository.UserRepository;
import com.project.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================= LOGIN =================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {

        Map<String, Object> response = new HashMap<>();

        String email = request.getEmail();
        String password = request.getPassword();

        // ================= ADMIN LOGIN =================
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getRole() == Role.ADMIN &&
                passwordEncoder.matches(password, user.getPassword())) {

                String token = jwtUtil.generateToken(user.getEmail());

                response.put("token", token);
                response.put("role", user.getRole());
                response.put("name", user.getName());
                response.put("email", user.getEmail());

                return response;
            }
        }

        // ================= TEACHER LOGIN =================
        Optional<Teacher> teacherOpt = teacherRepository.findByEmail(email);

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();

            if (passwordEncoder.matches(password, teacher.getPassword())) {

                String token = jwtUtil.generateToken(teacher.getEmail());

                response.put("token", token);
                response.put("role", "TEACHER");
                response.put("name", teacher.getName());
                response.put("email", teacher.getEmail());

                return response;
            }
        }

        // ================= STUDENT LOGIN =================
        Optional<Student> studentOpt = studentRepository.findByEmail(email);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();

            if (passwordEncoder.matches(password, student.getPassword())) {

                String token = jwtUtil.generateToken(student.getEmail());

                response.put("token", token);
                response.put("role", "STUDENT");
                response.put("name", student.getName());
                response.put("email", student.getEmail());

                return response;
            }
        }

        // ================= FAILURE =================
        throw new RuntimeException("Invalid credentials");
    }

    // ================= REGISTER ADMIN ONLY =================
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {

        Map<String, Object> response = new HashMap<>();

        if (user.getRole() != Role.ADMIN) {
            response.put("message", "Only admin registration is allowed here");
            return response;
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            response.put("message", "User already exists");
            return response;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        response.put("message", "Admin registered successfully");
        return response;
    }

    // ================= LOGIN REQUEST DTO =================
    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}