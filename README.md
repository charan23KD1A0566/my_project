# 📌 QR-Based Attendance System with Location Validation

## 📖 Overview

This project is a **QR Code based Attendance Management System** with **GPS location validation** to prevent proxy attendance.

It allows teachers to generate a QR code for a class session, and students can scan it to mark attendance only if they are within the allowed location range.

---

## 🚀 Features

### 🔐 Authentication

* JWT-based login system
* Role-based access (Admin, Teacher, Student)

### 🧑‍💼 Admin Module

* Add Departments
* Add Sections
* Add Teachers
* Add Students
* Add Subjects

### 🧑‍🏫 Teacher Module

* Generate QR Code for attendance
* Set:

  * Subject
  * Year & Section
  * Allowed radius (in meters)
  * Session expiry time
* Capture teacher location

### 👨‍🎓 Student Module

* Scan QR Code / Enter session details
* Capture student location
* Mark attendance
* View attendance history
* View attendance percentage

### 📍 Location-Based Validation

* Uses Haversine formula to calculate distance
* Attendance allowed only within defined radius

### 🔒 Security

* JWT Authentication
* No manual student/teacher ID input
* Backend validates identity using token

---

## 🛠️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring Security (JWT)
* MySQL

### Frontend

* HTML
* CSS
* JavaScript

### Tools

* Thunder Client / Postman (API Testing)
* Git & GitHub

---

## 🧠 System Workflow

### Teacher Flow

1. Login using credentials
2. Generate QR code with session details
3. System captures teacher location
4. QR code displayed with expiry timer

---

### Student Flow

1. Login using credentials
2. Scan QR code
3. System captures student location
4. Backend validates:

   * Session validity
   * Token
   * Expiry time
   * Distance from teacher
5. Attendance marked if valid

---

## 📍 Location Validation Logic

* Distance between teacher and student is calculated using **Haversine Formula**
* If:

  ```
  distance <= allowedRadius
  ```

  → Attendance marked
* Else:
  → Rejected

---

## 📂 Project Structure

```
QR-Attendance-System/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── security/
│   └── config/
│
├── frontend/
│   ├── login.html
│   ├── admin-dashboard.html
│   ├── teacher-dashboard.html
│   ├── student-dashboard.html
│   ├── css/
│   └── js/
│
├── README.md
└── .gitignore
```

---

## ⚙️ Setup Instructions

### 🔹 Backend Setup

1. Open backend folder
2. Configure database in `application.properties`
3. Run:

   ```
   mvn spring-boot:run
   ```

---

### 🔹 Frontend Setup

1. Open frontend folder
2. Open `login.html` in browser

---

## 🧪 API Testing

Use:

* Thunder Client (VS Code)
* Postman

### Example Login API:

```
POST /api/auth/login
```

```json
{
  "email": "teacher@gmail.com",
  "password": "teacher123"
}
```

---

## 🔐 JWT Authentication

* Token returned after login
* Sent in header:

  ```
  Authorization: Bearer <token>
  ```
* Backend extracts user from token

---

## 📊 Database Tables

* users
* departments
* sections
* teachers
* students
* subjects
* qr_sessions
* attendance

---

## ⚠️ Important Notes

* Location works only with HTTPS in frontend
* Do not expose JWT token in UI
* Backend handles all validations securely

---

## 🎯 Future Enhancements

* QR Scanner using camera
* Live map integration
* Attendance analytics dashboard
* Mobile app support

---

## 👨‍💻 Author

Developed as a full-stack project using Spring Boot and JavaScript.

---

## 📌 Conclusion

This system ensures:

* Secure attendance marking
* Prevention of proxy attendance
* Real-time validation using GPS

---

⭐ If you like this project, consider giving it a star!
