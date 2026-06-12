package com.project.controller;

import com.project.entity.*;
import com.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentAttendanceController {

    @Autowired
    private QRSessionRepository qrSessionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    // ✅ FINAL METHOD with Enhanced Error Handling
    @PostMapping("/mark-attendance")
    public ResponseEntity<?> markAttendance(@RequestBody MarkAttendanceRequest req,
                                            Authentication authentication) {

        try {
            // 🔐 Get logged-in student from JWT
            String email = authentication.getName();
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

            // 🔍 Get QR session
            QRSession session = qrSessionRepository.findById(req.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found with ID: " + req.getSessionId()));

            // Validate subject and section exist
            if (session.getSubject() == null) {
                return ResponseEntity.badRequest().body("Subject not found in session");
            }
            if (session.getSubject().getSection() == null) {
                return ResponseEntity.badRequest().body("Section not assigned to subject");
            }
            if (student.getSection() == null) {
                return ResponseEntity.badRequest().body("Student not assigned to any section");
            }

            // ❌ Token check
            if (!session.getToken().equals(req.getToken())) {
                return ResponseEntity.badRequest().body("Invalid QR token");
            }

            // ❌ Expiry check
            if (session.getExpiryTime().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("QR expired");
            }

            // ❌ Duplicate attendance check (optimized)
            boolean alreadyMarked = attendanceRepository
                    .existsByStudentIdAndSessionId(student.getId(), session.getId());

            if (alreadyMarked) {
                return ResponseEntity.badRequest().body("Attendance already marked");
            }

            // ❌ Section validation with detailed error message
            Long studentSectionId = student.getSection().getId();
            Long subjectSectionId = session.getSubject().getSection().getId();
            
            if (!studentSectionId.equals(subjectSectionId)) {
                return ResponseEntity.badRequest()
                    .body("Student not in this section. Student Section: " + studentSectionId + 
                          ", Subject Section: " + subjectSectionId);
            }

            // 📍 Location validation
            double distance = haversine(
                    session.getTeacherLatitude(),
                    session.getTeacherLongitude(),
                    req.getStudentLatitude(),
                    req.getStudentLongitude()
            );

            if (distance > session.getAllowedRadius()) {
                return ResponseEntity.badRequest()
                    .body("You are not in classroom range. Distance: " + distance + "m, Allowed: " + session.getAllowedRadius() + "m");
            }

            // ✅ Save attendance
            Attendance attendance = Attendance.builder()
                    .student(student)
                    .subject(session.getSubject())
                    .teacher(session.getTeacher())
                    .date(LocalDate.now())
                    .time(LocalTime.now())
                    .status(AttendanceStatus.PRESENT)
                    .session(session)
                    .build();

            attendanceRepository.save(attendance);

            return ResponseEntity.ok("Attendance marked successfully ✅");

        } catch (Exception e) {
            // Log the full exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Error: " + e.getMessage());
        }
    }

    // 📍 Haversine formula (distance in meters)
    private double haversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // 📦 CLEAN REQUEST DTO
    public static class MarkAttendanceRequest {
        private Long sessionId;
        private String token;
        private Double studentLatitude;
        private Double studentLongitude;

        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Double getStudentLatitude() { return studentLatitude; }
        public void setStudentLatitude(Double studentLatitude) { this.studentLatitude = studentLatitude; }

        public Double getStudentLongitude() { return studentLongitude; }
        public void setStudentLongitude(Double studentLongitude) { this.studentLongitude = studentLongitude; }
    }
}