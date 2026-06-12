package com.project.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
// Lombok removed; manual methods added
import java.util.List;

@Entity
@Table(name = "teachers")
// Lombok annotations removed
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<Subject> subjects;

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<QRSession> qrSessions;

    public Teacher() {}

    public Teacher(Long id, String name, String email, String password, Department department, List<Subject> subjects, List<QRSession> qrSessions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
        this.subjects = subjects;
        this.qrSessions = qrSessions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    public List<QRSession> getQrSessions() { return qrSessions; }
    public void setQrSessions(List<QRSession> qrSessions) { this.qrSessions = qrSessions; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private Department department;
        private List<Subject> subjects;
        private List<QRSession> qrSessions;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder department(Department department) { this.department = department; return this; }
        public Builder subjects(List<Subject> subjects) { this.subjects = subjects; return this; }
        public Builder qrSessions(List<QRSession> qrSessions) { this.qrSessions = qrSessions; return this; }
        public Teacher build() {
            return new Teacher(id, name, email, password, department, subjects, qrSessions);
        }
    }
}
