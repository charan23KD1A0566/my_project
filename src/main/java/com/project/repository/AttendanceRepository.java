package com.project.repository;

import com.project.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    boolean existsByStudentIdAndSessionId(Long studentId, Long sessionId);
    List<Attendance> findBySessionId(Long sessionId);
}