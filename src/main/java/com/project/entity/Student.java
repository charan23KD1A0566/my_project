package com.project.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
// Lombok removed; manual methods added
import java.util.List;

@Entity
@Table(name = "students")
// Lombok annotations removed
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String rollNumber;
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private int year;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<Attendance> attendances;

    public Student() {}

    public Student(Long id, String name, String rollNumber, String email, String password, Department department, int year, Section section, List<Attendance> attendances) {
        this.id = id;
        this.name = name;
        this.rollNumber = rollNumber;
        this.email = email;
        this.password = password;
        this.department = department;
        this.year = year;
        this.section = section;
        this.attendances = attendances;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private String name;
        private String rollNumber;
        private String email;
        private String password;
        private Department department;
        private int year;
        private Section section;
        private List<Attendance> attendances;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder department(Department department) { this.department = department; return this; }
        public Builder year(int year) { this.year = year; return this; }
        public Builder section(Section section) { this.section = section; return this; }
        public Builder attendances(List<Attendance> attendances) { this.attendances = attendances; return this; }
        public Student build() {
            return new Student(id, name, rollNumber, email, password, department, year, section, attendances);
        }
    }
}
