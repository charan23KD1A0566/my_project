package com.project.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
// Lombok removed; manual methods added
import java.util.List;

@Entity
@Table(name = "departments")
// Lombok annotations removed
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Section> sections;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Teacher> teachers;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Student> students;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Subject> subjects;

    public Department() {}

    public Department(Long id, String name, List<Section> sections, List<Teacher> teachers, List<Student> students, List<Subject> subjects) {
        this.id = id;
        this.name = name;
        this.sections = sections;
        this.teachers = teachers;
        this.students = students;
        this.subjects = subjects;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }

    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private String name;
        private List<Section> sections;
        private List<Teacher> teachers;
        private List<Student> students;
        private List<Subject> subjects;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder sections(List<Section> sections) { this.sections = sections; return this; }
        public Builder teachers(List<Teacher> teachers) { this.teachers = teachers; return this; }
        public Builder students(List<Student> students) { this.students = students; return this; }
        public Builder subjects(List<Subject> subjects) { this.subjects = subjects; return this; }
        public Department build() {
            return new Department(id, name, sections, teachers, students, subjects);
        }
    }
}
