package com.project.controller;

import com.project.entity.User;
import com.project.entity.Teacher;
import com.project.entity.Student;
import com.project.repository.UserRepository;
import com.project.repository.TeacherRepository;
import com.project.repository.StudentRepository;
import com.project.repository.SectionRepository;
import com.project.repository.AttendanceRepository;
import com.project.repository.QRSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private QRSessionRepository qrSessionRepository;
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        long adminCount = userRepository.count();
        long teacherCount = teacherRepository.count();
        long studentCount = studentRepository.count();
        long totalUsers = adminCount + teacherCount + studentCount;
        long todayPresent = attendanceRepository.findAll().stream()
                .filter(a -> LocalDate.now().equals(a.getDate()))
                .count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalUsers", totalUsers);
        result.put("totalClasses", sectionRepository.count());
        result.put("todayAttendance", studentCount == 0 ? "0%" : String.format(Locale.US, "%.1f%%", (todayPresent * 100.0) / studentCount));
        result.put("qrScans", qrSessionRepository.count());
        result.put("totalTeachers", teacherCount);
        result.put("totalStudents", studentCount);
        result.put("totalAdmins", adminCount);
        return result;
    }

/**
     * Get all users (combines admins, teachers, and students)
     */
    @GetMapping("/users")
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> allUsers = new ArrayList<>();

        // Get all admins/users
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getRole());
            userMap.put("type", "ADMIN");
            allUsers.add(userMap);
        }

        // Get all teachers
        List<Teacher> teachers = teacherRepository.findAll();
        for (Teacher teacher : teachers) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", teacher.getId());
            userMap.put("email", teacher.getEmail());
            userMap.put("name", teacher.getName());
            userMap.put("role", "TEACHER");
            userMap.put("type", "TEACHER");
            allUsers.add(userMap);
        }

        // Get all students
        List<Student> students = studentRepository.findAll();
        for (Student student : students) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", student.getId());
            userMap.put("email", student.getEmail());
            userMap.put("name", student.getName());
            userMap.put("rollNumber", student.getRollNumber());
            userMap.put("role", "STUDENT");
            userMap.put("type", "STUDENT");
            allUsers.add(userMap);
        }

        return allUsers;
    }

    /**
     * Get all users (simplified count for tests)
     */
    @GetMapping("/users/count")
    public Map<String, Integer> getUsersCount() {
        int totalUsers = 0;
        totalUsers += userRepository.findAll().size();
        totalUsers += teacherRepository.findAll().size();
        totalUsers += studentRepository.findAll().size();

        Map<String, Integer> result = new HashMap<>();
        result.put("count", totalUsers);
        return result;
    }
}


