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
                return ResponseEntity.badRequest().body(java.util.Map.of("status", "ERROR", "message", "Subject not found in session"));
            }
            if (session.getSubject().getSection() == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("status", "ERROR", "message", "Section not assigned to subject"));
            }
            if (student.getSection() == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("status", "ERROR", "message", "Student not assigned to any section"));
            }

            // ❌ Token check
            if (!session.getToken().equals(req.getToken())) {
                return ResponseEntity.badRequest().body(java.util.Map.of("status", "ERROR", "message", "Invalid QR token"));
            }

            // ❌ Expiry check
            if (session.getExpiryTime().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(java.util.Map.of("status", "ERROR", "message", "QR expired"));
            }

            // ❌ Duplicate attendance check (optimized)
            boolean alreadyMarked = attendanceRepository
                    .existsByStudentIdAndSessionId(student.getId(), session.getId());

            if (alreadyMarked) {
                return ResponseEntity.badRequest().body(java.util.Map.of("status", "ERROR", "message", "Attendance already marked"));
            }

            // ❌ Section validation with detailed error message
            Long studentSectionId = student.getSection().getId();
            Long subjectSectionId = session.getSubject().getSection().getId();
            
            if (!studentSectionId.equals(subjectSectionId)) {
                return ResponseEntity.badRequest()
                    .body(java.util.Map.of("status", "ERROR", "message", "Student not in this section. Student Section: " + studentSectionId + ", Subject Section: " + subjectSectionId));
            }

            // 📍 Location validation
            double distance = haversine(
                    session.getTeacherLatitude(),
                    session.getTeacherLongitude(),
                    req.getLatitude(),
                    req.getLongitude()
            );

            if (distance > session.getAllowedRadius()) {
                return ResponseEntity.badRequest()
                    .body(java.util.Map.of("status", "ERROR", "message", "You are not in classroom range. Distance: " + distance + "m, Allowed: " + session.getAllowedRadius() + "m"));
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

            return ResponseEntity.ok().body(java.util.Map.of("status", "PRESENT", "message", "Attendance marked successfully ✅"));

        } catch (Exception e) {
            // Log the full exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(java.util.Map.of("status", "ERROR", "message", "Error: " + e.getMessage()));
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

    // ✅ GET ATTENDANCE HISTORY
    @GetMapping("/attendance-history")
    public ResponseEntity<?> getAttendanceHistory(Authentication authentication) {
        try {
            String email = authentication.getName();
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

            var attendanceList = attendanceRepository.findByStudentId(student.getId());
            
            var response = attendanceList.stream().map(a -> new java.util.HashMap<String, Object>() {{
                put("id", a.getId());
                put("subjectName", a.getSubject().getName());
                put("teacherName", a.getTeacher().getName());
                put("date", a.getDate());
                put("time", a.getTime());
                put("status", a.getStatus());
                put("markedTime", a.getTime());
            }}).toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // 📦 CLEAN REQUEST DTO
    public static class MarkAttendanceRequest {
        private Long sessionId;
        private String token;
        private Double latitude;
        private Double longitude;

        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }

        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        
        // Compatibility aliases
        public Double getStudentLatitude() { return latitude; }
        public void setStudentLatitude(Double studentLatitude) { this.latitude = studentLatitude; }

        public Double getStudentLongitude() { return longitude; }
        public void setStudentLongitude(Double studentLongitude) { this.longitude = studentLongitude; }
    }
}