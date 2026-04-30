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
@PreAuthorize("hasRole('TEACHER')")
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
    public ResponseEntity<?> generateQR(@RequestBody GenerateQRRequest req)
            throws WriterException, IOException {

        Optional<Teacher> teacherOpt = teacherRepository.findById(req.getTeacherId());
        if (teacherOpt.isEmpty() ||
                !teacherOpt.get().getName().equals(req.getTeacherName())) {
            return ResponseEntity.badRequest().body("Invalid teacher");
        }

        Optional<Subject> subjectOpt = subjectRepository.findById(req.getSubjectId());
        if (subjectOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid subject");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Long expiryMinutes = req.getQrExpiryTime();
        if (expiryMinutes == null || expiryMinutes <= 0) {
            expiryMinutes = 5L;
        }

        LocalDateTime expiry = now.plusMinutes(expiryMinutes);

        QRSession session = QRSession.builder()
                .teacher(teacherOpt.get())
                .subject(subjectOpt.get())
                .token(token)
                .generatedTime(now)
                .expiryTime(expiry)
                .teacherLatitude(req.getTeacherLatitude())
                .teacherLongitude(req.getTeacherLongitude())
                .allowedRadius(50.0)
                .build();

        qrSessionRepository.save(session);

        String qrContent = "sessionId:" + session.getId() + ",token:" + token;
        String qrBase64 = generateQRBase64(qrContent);

        GenerateQRResponse resp = new GenerateQRResponse();
        resp.setSessionId(session.getId());
        resp.setToken(token);
        resp.setExpiryTime(expiry);
        resp.setQrImageBase64(qrBase64);

        return ResponseEntity.ok(resp);
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
        private int year;
        private String section;
        private String department;
        private Double teacherLatitude;
        private Double teacherLongitude;
        private Long qrExpiryTime;

        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }

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