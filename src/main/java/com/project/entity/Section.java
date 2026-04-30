package com.project.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
// Lombok removed; manual methods added
import java.util.List;

@Entity
@Table(name = "sections")
// Lombok annotations removed
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private int year;
    private String sectionName;

    @OneToMany(mappedBy = "section")
    @JsonIgnore
    private List<Student> students;

    @OneToMany(mappedBy = "section")
    @JsonIgnore
    private List<Subject> subjects;

    public Section() {}

    public Section(Long id, Department department, int year, String sectionName, List<Student> students, List<Subject> subjects) {
        this.id = id;
        this.department = department;
        this.year = year;
        this.sectionName = sectionName;
        this.students = students;
        this.subjects = subjects;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private Department department;
        private int year;
        private String sectionName;
        private List<Student> students;
        private List<Subject> subjects;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder department(Department department) { this.department = department; return this; }
        public Builder year(int year) { this.year = year; return this; }
        public Builder sectionName(String sectionName) { this.sectionName = sectionName; return this; }
        public Builder students(List<Student> students) { this.students = students; return this; }
        public Builder subjects(List<Subject> subjects) { this.subjects = subjects; return this; }
        public Section build() {
            return new Section(id, department, year, sectionName, students, subjects);
        }
    }
}
