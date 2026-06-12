package com.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "🎓 QR Attendance Management System - API Running ✅\n" +
               "📍 Endpoints: /api/students, /api/teachers, /api/departments, /api/auth/login";
    }

    @GetMapping("/health")
    public String health() {
        return "API is healthy ✅";
    }
}
