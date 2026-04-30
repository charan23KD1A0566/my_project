package com.project.controller;

import com.project.entity.Attendance;
import com.project.entity.Student;
import com.project.repository.AttendanceRepository;
import com.project.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/attendance")
@PreAuthorize("hasRole('STUDENT')")
public class StudentAttendanceViewController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ✅ FIXED METHOD
    @GetMapping("/me")
    public ResponseEntity<?> getMyAttendance(Authentication authentication) {

        // 🔐 Get logged-in user email from JWT
        String email = authentication.getName();

        // 🔍 Find student using email
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Student not found");
        }

        Student student = studentOpt.get();

        // ⚡ Fetch only this student's attendance (performance fix)
        List<Attendance> all = attendanceRepository.findByStudentId(student.getId());

        // 📊 Group by subject
        Map<String, List<Attendance>> bySubject =
                all.stream().collect(Collectors.groupingBy(a -> a.getSubject().getName()));

        List<SubjectAttendance> subjectAttendanceList = new ArrayList<>();

        for (Map.Entry<String, List<Attendance>> entry : bySubject.entrySet()) {

            long present = entry.getValue().stream()
                    .filter(a -> a.getStatus().name().equals("PRESENT"))
                    .count();

            long total = entry.getValue().size();

            double percent = total > 0 ? (present * 100.0 / total) : 0.0;

            subjectAttendanceList.add(
                    new SubjectAttendance(entry.getKey(), percent)
            );
        }

        // 📅 History
        List<AttendanceHistory> history = all.stream().map(a -> {
            AttendanceHistory h = new AttendanceHistory();
            h.setSubject(a.getSubject().getName());
            h.setDate(a.getDate());
            h.setTime(a.getTime());
            h.setStatus(a.getStatus().name());
            return h;
        }).collect(Collectors.toList());

        // 📦 Response
        StudentAttendanceResponse resp = new StudentAttendanceResponse();
        resp.setSubjectAttendance(subjectAttendanceList);
        resp.setHistory(history);

        return ResponseEntity.ok(resp);
    }

    // ================= DTO CLASSES =================

    public static class StudentAttendanceResponse {
        private List<SubjectAttendance> subjectAttendance;
        private List<AttendanceHistory> history;

        public List<SubjectAttendance> getSubjectAttendance() {
            return subjectAttendance;
        }

        public void setSubjectAttendance(List<SubjectAttendance> subjectAttendance) {
            this.subjectAttendance = subjectAttendance;
        }

        public List<AttendanceHistory> getHistory() {
            return history;
        }

        public void setHistory(List<AttendanceHistory> history) {
            this.history = history;
        }
    }

    public static class SubjectAttendance {
        private String subject;
        private double percentage;

        public SubjectAttendance(String subject, double percentage) {
            this.subject = subject;
            this.percentage = percentage;
        }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    public static class AttendanceHistory {
        private String subject;
        private java.time.LocalDate date;
        private java.time.LocalTime time;
        private String status;

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public java.time.LocalDate getDate() { return date; }
        public void setDate(java.time.LocalDate date) { this.date = date; }

        public java.time.LocalTime getTime() { return time; }
        public void setTime(java.time.LocalTime time) { this.time = time; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}