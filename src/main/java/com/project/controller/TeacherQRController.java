package com.project.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import com.project.entity.*;
import com.project.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/teacher")
public class TeacherQRController {

    // ================= REPOSITORIES =================
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private QRSessionRepository qrSessionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ================= QR GENERATION =================
    @PostMapping("/generateQR")
    public ResponseEntity<?> generateQR(@RequestBody GenerateQRRequest req) {
        try {
            System.out.println("🔍 QR Request received: " + (req != null ? "not null" : "null"));
            if (req != null) {
                System.out.println("  teacherId: " + req.getTeacherId());
                System.out.println("  teacherName: " + req.getTeacherName());
                System.out.println("  subjectId: " + req.getSubjectId());
                System.out.println("  year: " + req.getYear());
                System.out.println("  section: " + req.getSection());
            }
            
            // Validate required fields
            if (req.getTeacherId() == null) {
                System.out.println("❌ teacherId is null");
                return ResponseEntity.badRequest().body("teacherId is required");
            }
            System.out.println("✅ teacherId validated");
            
            if (req.getTeacherName() == null || req.getTeacherName().isEmpty()) {
                System.out.println("❌ teacherName is null or empty");
                return ResponseEntity.badRequest().body("teacherName is required");
            }
            System.out.println("✅ teacherName validated");
            
            if (req.getSubjectId() == null) {
                System.out.println("❌ subjectId is null");
                return ResponseEntity.badRequest().body("subjectId is required");
            }
            System.out.println("✅ subjectId validated");
            
            if (req.getTeacherLatitude() == null || req.getTeacherLongitude() == null) {
                System.out.println("❌ latitude or longitude is null");
                return ResponseEntity.badRequest().body("Teacher latitude and longitude are required");
            }
            System.out.println("✅ location validated");

            System.out.println("🔍 Fetching teacher...");
            Optional<Teacher> teacherOpt = teacherRepository.findById(req.getTeacherId());
            if (teacherOpt.isEmpty()) {
                System.out.println("❌ Teacher not found");
                return ResponseEntity.badRequest().body("Teacher not found with ID: " + req.getTeacherId());
            }
            System.out.println("✅ Teacher found");
            
            if (!teacherOpt.get().getName().equals(req.getTeacherName())) {
                System.out.println("❌ Teacher name mismatch");
                return ResponseEntity.badRequest().body("Teacher name mismatch. Found: " + teacherOpt.get().getName() + ", Expected: " + req.getTeacherName());
            }
            System.out.println("✅ Teacher name matched");

            System.out.println("🔍 Fetching subject with ID: " + req.getSubjectId());
            System.out.println("🔍 Total subjects in DB: " + subjectRepository.count());
            Optional<Subject> subjectOpt = subjectRepository.findById(req.getSubjectId());
            if (subjectOpt.isEmpty()) {
                System.out.println("❌ Subject not found with ID: " + req.getSubjectId());
                System.out.println("📋 Available subjects (first 5):");
                subjectRepository.findAll().stream().limit(5).forEach(s -> 
                    System.out.println("   ID: " + s.getId() + ", Name: " + s.getName())
                );
                return ResponseEntity.badRequest().body("Subject not found with ID: " + req.getSubjectId());
            }
            System.out.println("✅ Subject found: " + subjectOpt.get().getName());

            String token = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();

            Long expiryDurationInSeconds = req.getExpiryDurationInSeconds();
            LocalDateTime expiry;
            if (expiryDurationInSeconds != null && expiryDurationInSeconds > 0) {
                expiry = now.plusSeconds(expiryDurationInSeconds);
                System.out.println("QR Expiry: " + expiryDurationInSeconds + " seconds");
            } else {
                Long expiryMinutes = req.getQrExpiryTime();
                if (expiryMinutes == null || expiryMinutes <= 0) {
                    expiryMinutes = 5L;
                }
                expiry = now.plusMinutes(expiryMinutes);
                System.out.println("QR Expiry: " + expiryMinutes + " minutes");
            }

            QRSession session = QRSession.builder()
                    .teacher(teacherOpt.get())
                    .subject(subjectOpt.get())
                    .token(token)
                    .generatedTime(now)
                    .expiryTime(expiry)
                    .teacherLatitude(req.getTeacherLatitude())
                    .teacherLongitude(req.getTeacherLongitude())
                    .allowedRadius(req.getAllowedRadius() != null ? req.getAllowedRadius() : 50.0)
                    .build();

            System.out.println("🔍 Saving QRSession...");
            qrSessionRepository.save(session);
            System.out.println("✅ QRSession saved with ID: " + session.getId());

            String qrContent = "sessionId:" + session.getId() + ",token:" + token;
            System.out.println("🔍 QR Content: " + qrContent);
            
            System.out.println("🔍 Generating QR Base64...");
            String qrBase64 = generateQRBase64(qrContent);
            System.out.println("✅ QR Base64 generated, length: " + qrBase64.length());

            GenerateQRResponse resp = new GenerateQRResponse();
            resp.setSessionId(session.getId());
            resp.setToken(token);
            resp.setExpiryTime(expiry);
            resp.setQrImageBase64(qrBase64);
            
            System.out.println("✅ Returning QR response");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            System.out.println("❌ Exception caught: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("QR Generation Error: " + e.getMessage());
        }
    }

    // ================= ATTENDANCE API =================
    @GetMapping("/session/{sessionId}/attendance")
    public ResponseEntity<?> getSessionAttendance(@PathVariable Long sessionId) {

        Optional<QRSession> sessionOpt = qrSessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Session not found");
        }

        QRSession session = sessionOpt.get();

        List<Student> students =
                studentRepository.findBySectionId(
                        session.getSubject().getSection().getId()
                );

        List<Attendance> attendanceList =
                attendanceRepository.findBySessionId(sessionId);

        Map<Long, Attendance> attendanceMap = new HashMap<>();
        for (Attendance a : attendanceList) {
            attendanceMap.put(a.getStudent().getId(), a);
        }

        List<StudentAttendanceStatus> result = new ArrayList<>();

        for (Student s : students) {
            Attendance a = attendanceMap.get(s.getId());

            result.add(new StudentAttendanceStatus(
                    s.getId(),
                    s.getName(),
                    a != null ? a.getStatus().name() : "ABSENT"
            ));
        }

        return ResponseEntity.ok(result);
    }

    // ================= QR GENERATOR =================
    private String generateQRBase64(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    // ================= DTO: REQUEST =================
    public static class GenerateQRRequest {
        private Long teacherId;
        private String teacherName;
        private Long subjectId;
        private Integer year;
        private String section;
        private String department;
        private Double teacherLatitude;
        private Double teacherLongitude;
        private Long qrExpiryTime;
        private Long expiryDurationInSeconds;
        private Double allowedRadius;

        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }

        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public Double getTeacherLatitude() { return teacherLatitude; }
        public void setTeacherLatitude(Double teacherLatitude) { this.teacherLatitude = teacherLatitude; }

        public Double getTeacherLongitude() { return teacherLongitude; }
        public void setTeacherLongitude(Double teacherLongitude) { this.teacherLongitude = teacherLongitude; }

        public Long getQrExpiryTime() { return qrExpiryTime; }
        public void setQrExpiryTime(Long qrExpiryTime) { this.qrExpiryTime = qrExpiryTime; }
        public Long getExpiryDurationInSeconds() { return expiryDurationInSeconds; }
        public void setExpiryDurationInSeconds(Long expiryDurationInSeconds) { this.expiryDurationInSeconds = expiryDurationInSeconds; }

        public Double getAllowedRadius() { return allowedRadius; }
        public void setAllowedRadius(Double allowedRadius) { this.allowedRadius = allowedRadius; }
    }

    // ================= DTO: RESPONSE =================
    public static class GenerateQRResponse {
        private Long sessionId;
        private String token;
        private LocalDateTime expiryTime;
        private String qrImageBase64;

        public Long getSessionId() { return sessionId; }
        public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public LocalDateTime getExpiryTime() { return expiryTime; }
        public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

        public String getQrImageBase64() { return qrImageBase64; }
        public void setQrImageBase64(String qrImageBase64) { this.qrImageBase64 = qrImageBase64; }
    }

    // ================= INNER CLASS =================
    public static class StudentAttendanceStatus {
        private Long studentId;
        private String studentName;
        private String status;

        public StudentAttendanceStatus(Long studentId, String studentName, String status) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.status = status;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}




