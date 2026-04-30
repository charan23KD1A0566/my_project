package com.project.entity;

import jakarta.persistence.*;
// Lombok removed; manual methods added
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_sessions")
// Lombok annotations removed
public class QRSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    private String token;
    private LocalDateTime generatedTime;
    private LocalDateTime expiryTime;

    private Double teacherLatitude;
    private Double teacherLongitude;

    private Double allowedRadius = 50.0;

    public QRSession() {}

    public QRSession(Long id, Teacher teacher, Subject subject, String token, LocalDateTime generatedTime, LocalDateTime expiryTime, Double teacherLatitude, Double teacherLongitude, Double allowedRadius) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.token = token;
        this.generatedTime = generatedTime;
        this.expiryTime = expiryTime;
        this.teacherLatitude = teacherLatitude;
        this.teacherLongitude = teacherLongitude;
        this.allowedRadius = allowedRadius;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(LocalDateTime generatedTime) { this.generatedTime = generatedTime; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public Double getTeacherLatitude() { return teacherLatitude; }
    public void setTeacherLatitude(Double teacherLatitude) { this.teacherLatitude = teacherLatitude; }

    public Double getTeacherLongitude() { return teacherLongitude; }
    public void setTeacherLongitude(Double teacherLongitude) { this.teacherLongitude = teacherLongitude; }

    public Double getAllowedRadius() { return allowedRadius; }
    public void setAllowedRadius(Double allowedRadius) { this.allowedRadius = allowedRadius; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private Teacher teacher;
        private Subject subject;
        private String token;
        private LocalDateTime generatedTime;
        private LocalDateTime expiryTime;
        private Double teacherLatitude;
        private Double teacherLongitude;
        private Double allowedRadius = 50.0;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder teacher(Teacher teacher) { this.teacher = teacher; return this; }
        public Builder subject(Subject subject) { this.subject = subject; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder generatedTime(LocalDateTime generatedTime) { this.generatedTime = generatedTime; return this; }
        public Builder expiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; return this; }
        public Builder teacherLatitude(Double teacherLatitude) { this.teacherLatitude = teacherLatitude; return this; }
        public Builder teacherLongitude(Double teacherLongitude) { this.teacherLongitude = teacherLongitude; return this; }
        public Builder allowedRadius(Double allowedRadius) { this.allowedRadius = allowedRadius; return this; }
        public QRSession build() {
            return new QRSession(id, teacher, subject, token, generatedTime, expiryTime, teacherLatitude, teacherLongitude, allowedRadius);
        }
    }
}
