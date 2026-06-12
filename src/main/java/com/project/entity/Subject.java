package com.project.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
// Lombok removed; manual methods added
import java.util.List;

@Entity
@Table(name = "subjects")
// Lombok annotations removed
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private int year;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @OneToMany(mappedBy = "subject")
    @JsonIgnore
    private List<QRSession> qrSessions;

    @OneToMany(mappedBy = "subject")
    @JsonIgnore
    private List<Attendance> attendances;

    public Subject() {}

    public Subject(Long id, String name, Teacher teacher, Department department, int year, Section section, List<QRSession> qrSessions, List<Attendance> attendances) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.department = department;
        this.year = year;
        this.section = section;
        this.qrSessions = qrSessions;
        this.attendances = attendances;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public List<QRSession> getQrSessions() { return qrSessions; }
    public void setQrSessions(List<QRSession> qrSessions) { this.qrSessions = qrSessions; }

    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private String name;
        private Teacher teacher;
        private Department department;
        private int year;
        private Section section;
        private List<QRSession> qrSessions;
        private List<Attendance> attendances;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder teacher(Teacher teacher) { this.teacher = teacher; return this; }
        public Builder department(Department department) { this.department = department; return this; }
        public Builder year(int year) { this.year = year; return this; }
        public Builder section(Section section) { this.section = section; return this; }
        public Builder qrSessions(List<QRSession> qrSessions) { this.qrSessions = qrSessions; return this; }
        public Builder attendances(List<Attendance> attendances) { this.attendances = attendances; return this; }
        public Subject build() {
            return new Subject(id, name, teacher, department, year, section, qrSessions, attendances);
        }
    }
}
