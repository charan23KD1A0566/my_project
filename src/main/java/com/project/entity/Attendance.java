package com.project.entity;

import jakarta.persistence.*;
// Lombok removed; manual methods added
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendances")
// Lombok annotations removed
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    private LocalDate date;
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private QRSession session;

    public Attendance() {}

    public Attendance(Long id, Student student, Subject subject, Teacher teacher, LocalDate date, LocalTime time, AttendanceStatus status, QRSession session) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.teacher = teacher;
        this.date = date;
        this.time = time;
        this.status = status;
        this.session = session;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public QRSession getSession() { return session; }
    public void setSession(QRSession session) { this.session = session; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private Student student;
        private Subject subject;
        private Teacher teacher;
        private LocalDate date;
        private LocalTime time;
        private AttendanceStatus status;
        private QRSession session;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder student(Student student) { this.student = student; return this; }
        public Builder subject(Subject subject) { this.subject = subject; return this; }
        public Builder teacher(Teacher teacher) { this.teacher = teacher; return this; }
        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder time(LocalTime time) { this.time = time; return this; }
        public Builder status(AttendanceStatus status) { this.status = status; return this; }
        public Builder session(QRSession session) { this.session = session; return this; }
        public Attendance build() {
            return new Attendance(id, student, subject, teacher, date, time, status, session);
        }
    }
}
