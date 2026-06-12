package com.project.config;

import com.project.entity.*;
import com.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
public class DataInitializer {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private SectionRepository sectionRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Bean
    public CommandLineRunner initData(PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Create Admin User
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }

            // 2. Create Departments
            Map<String, Department> departments = new HashMap<>();
            String[] deptNames = {"Computer Science", "Electronics", "Mechanical", "Civil"};
            String[] deptCodes = {"CS", "EC", "ME", "CE"};
            List<Department> allDepts = departmentRepository.findAll();
            
            for (int i = 0; i < deptNames.length; i++) {
                final String deptName = deptNames[i];
                Department existingDept = allDepts.stream()
                        .filter(d -> d.getName().equals(deptName))
                        .findFirst()
                        .orElse(null);
                
                if (existingDept == null) {
                    Department dept = new Department();
                    dept.setName(deptName);
                    departmentRepository.save(dept);
                    departments.put(deptCodes[i], dept);
                } else {
                    departments.put(deptCodes[i], existingDept);
                }
            }

            // 3. Create Sections for each Department and Year
            Map<String, Section> sections = new HashMap<>();
            List<Section> allSections = sectionRepository.findAll();
            
            for (Department dept : departments.values()) {
                for (int year = 1; year <= 4; year++) {
                    for (String sectionChar : Arrays.asList("A", "B", "C")) {
                        String sectionKey = dept.getName().substring(0, 2).toUpperCase() + year + sectionChar;
                        
                        final int finalYear = year;
                        final String finalSectionChar = sectionChar;
                        final Long deptId = dept.getId();
                        
                        Section existingSection = allSections.stream()
                                .filter(s -> s.getDepartment().getId().equals(deptId) 
                                    && s.getYear() == finalYear 
                                    && s.getSectionName().equals(finalSectionChar))
                                .findFirst()
                                .orElse(null);
                        
                        if (existingSection == null) {
                            Section section = new Section();
                            section.setDepartment(dept);
                            section.setYear(year);
                            section.setSectionName(sectionChar);
                            sectionRepository.save(section);
                            sections.put(sectionKey, section);
                        }
                    }
                }
            }

            // 4. Create Teachers for each Department
            int teacherId = 1;
            List<Teacher> allTeachers = teacherRepository.findAll();
            
            for (Department dept : departments.values()) {
                for (int i = 0; i < 5; i++) {
                    String email = "teacher" + teacherId + "@college.com";
                    final String finalEmail = email;
                    
                    boolean exists = allTeachers.stream()
                            .anyMatch(t -> t.getEmail().equals(finalEmail));
                    
                    if (!exists) {
                        Teacher teacher = new Teacher();
                        teacher.setName("Dr. " + dept.getName().substring(0, 1) + "Teacher" + i);
                        teacher.setEmail(email);
                        teacher.setPassword(passwordEncoder.encode("teacher123"));
                        teacher.setDepartment(dept);
                        teacherRepository.save(teacher);
                    }
                    teacherId++;
                }
            }

            // 5. Create Subjects for each Department and assign to Sections
            Map<String, Subject> subjects = new HashMap<>();
            Map<String, String[]> deptSubjects = new HashMap<>();
            deptSubjects.put("CS", new String[]{"Data Structures", "DBMS", "Web Development", "AI/ML", "Cybersecurity"});
            deptSubjects.put("EC", new String[]{"Digital Electronics", "Signals & Systems", "Microcontrollers", "Power Electronics", "Communication Systems"});
            deptSubjects.put("ME", new String[]{"Thermodynamics", "Mechanics", "Machine Design", "Fluid Mechanics", "Manufacturing"});
            deptSubjects.put("CE", new String[]{"Structural Analysis", "Concrete Technology", "Geotechnical Engineering", "Transportation", "Hydraulics"});

            List<Subject> allSubjects = subjectRepository.findAll();
            
            for (Map.Entry<String, String[]> entry : deptSubjects.entrySet()) {
                Department dept = departments.get(entry.getKey());
                List<Teacher> teachersInDept = teacherRepository.findAll().stream()
                        .filter(t -> t.getDepartment().getId().equals(dept.getId()))
                        .toList();
                
                // 🔥 CRITICAL FIX: Assign subjects to all sections of this department
                List<Section> sectionsInDept = sectionRepository.findAll().stream()
                        .filter(s -> s.getDepartment().getId().equals(dept.getId()))
                        .toList();
                
                int subjectIdx = 0;
                for (String subjectName : entry.getValue()) {
                    final String finalSubjectName = subjectName;
                    final Long finalDeptId = dept.getId();
                    
                    // Check if this subject exists in ANY section of this department
                    boolean subjectExists = allSubjects.stream()
                            .anyMatch(s -> s.getName().equals(finalSubjectName) && 
                                s.getDepartment().getId().equals(finalDeptId) &&
                                s.getSection() != null);
                    
                    if (!subjectExists) {
                        // Create one subject per section (round-robin through teachers)
                        for (Section section : sectionsInDept) {
                            Subject subject = new Subject();
                            subject.setName(subjectName);
                            subject.setDepartment(dept);
                            subject.setYear(section.getYear());  // ✅ Assign year
                            subject.setSection(section);  // ✅ CRITICAL: Assign to section!
                            if (!teachersInDept.isEmpty()) {
                                subject.setTeacher(teachersInDept.get(subjectIdx % teachersInDept.size()));
                            }
                            subjectRepository.save(subject);
                            subjects.put(entry.getKey() + "_" + section.getId() + "_" + subjectName, subject);
                        }
                    }
                    subjectIdx++;
                }
            }

            // 6. Create Students for each Section
            int studentId = 1;
            List<Student> allStudents = studentRepository.findAll();
            
            for (Section section : sectionRepository.findAll()) {
                for (int i = 1; i <= 50; i++) {
                    String rollNumber = section.getDepartment().getName().substring(0, 2).toUpperCase() 
                        + section.getYear() + section.getSectionName() + String.format("%03d", i);
                    String email = "student" + studentId + "@college.com";
                    final String finalEmail = email;
                    
                    boolean studentExists = allStudents.stream()
                            .anyMatch(s -> s.getEmail().equals(finalEmail));
                    
                    if (!studentExists) {
                        Student student = new Student();
                        student.setName("Student " + studentId);
                        student.setRollNumber(rollNumber);
                        student.setEmail(email);
                        student.setPassword(passwordEncoder.encode("student123"));
                        student.setDepartment(section.getDepartment());
                        student.setYear(section.getYear());
                        student.setSection(section);
                        studentRepository.save(student);
                    }
                    studentId++;
                }
            }

            System.out.println("✅ Sample data initialized successfully!");
            System.out.println("📊 Data Summary:");
            System.out.println("   - Departments: " + departmentRepository.count());
            System.out.println("   - Sections: " + sectionRepository.count());
            System.out.println("   - Teachers: " + teacherRepository.count());
            System.out.println("   - Students: " + studentRepository.count());
            System.out.println("   - Subjects: " + subjectRepository.count());
        };
    }
}
