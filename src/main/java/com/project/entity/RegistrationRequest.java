package com.project.entity;

import jakarta.persistence.*;
// Lombok removed; manual methods added

@Entity
@Table(name = "registration_requests")
// Lombok annotations removed
public class RegistrationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String role; // STUDENT or TEACHER
    private String rollNumber; // for students only
    private boolean approved;

    public RegistrationRequest() {}

    public RegistrationRequest(Long id, String name, String email, String password, String role, String rollNumber, boolean approved) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.rollNumber = rollNumber;
        this.approved = approved;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String role;
        private String rollNumber;
        private boolean approved;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder rollNumber(String rollNumber) { this.rollNumber = rollNumber; return this; }
        public Builder approved(boolean approved) { this.approved = approved; return this; }
        public RegistrationRequest build() {
            return new RegistrationRequest(id, name, email, password, role, rollNumber, approved);
        }
    }
}
