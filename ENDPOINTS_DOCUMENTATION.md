# QR Attendance Management System - Complete API Endpoints

## 📋 Overview
Base URL: `https://my-project-80ir.onrender.com/api` (Production) or `http://localhost:8080/api` (Development)

---

## 🔓 Public Endpoints (No Authentication Required)

### Home & Health Checks
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | API status message |
| GET | `/health` | Health check endpoint |

### Student Directory (Public Access)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/students` | Get all students |
| GET | `/students/{id}` | Get student by ID |
| GET | `/students/department/{departmentId}` | Get students by department |
| GET | `/students/section/{sectionId}` | Get students by section |

---

## 🔐 Authentication Endpoints (Any User)

### Login & Registration
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|----------------|
| POST | `/auth/login` | Login (Admin/Teacher/Student) | ❌ No |
| POST | `/auth/register` | Register admin only | ❌ No |
| POST | `/register` | Submit teacher/student registration request | ❌ No |

**Request Body (Auth/Login):**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (Auth/Login):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "role": "ADMIN|TEACHER|STUDENT",
  "name": "User Name",
  "email": "user@example.com"
}
```

---

## 👨‍💼 Admin Endpoints (Requires ADMIN Role)

### Department Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/departments` | Get all departments |
| POST | `/admin/departments` | Create new department |
| PUT | `/admin/departments/{id}` | Update department |
| DELETE | `/admin/departments/{id}` | Delete department |

### Teacher Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/teachers` | Get all teachers |
| POST | `/admin/teachers` | Create new teacher |
| PUT | `/admin/teachers/{id}` | Update teacher |
| DELETE | `/admin/teachers/{id}` | Delete teacher |

### Student Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/students` | Get all students |
| POST | `/admin/students` | Create new student |
| PUT | `/admin/students/{id}` | Update student |
| DELETE | `/admin/students/{id}` | Delete student |

### Subject Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/subjects` | Get all subjects |
| POST | `/admin/subjects` | Create new subject |
| PUT | `/admin/subjects/{id}` | Update subject |
| DELETE | `/admin/subjects/{id}` | Delete subject |

### Section Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/sections` | Get all sections |
| POST | `/admin/sections` | Create new section |
| PUT | `/admin/sections/{id}` | Update section |
| DELETE | `/admin/sections/{id}` | Delete section |

### Year Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/years` | Get all years (1,2,3,4) |
| POST | `/admin/years` | Add new year |
| DELETE | `/admin/years/{year}` | Delete year |

### Registration Request Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/registration-requests` | Get pending registration requests |
| POST | `/admin/registration-requests/approve/{id}` | Approve registration request |
| DELETE | `/admin/registration-requests/reject/{id}` | Reject registration request |

---

## 👨‍🏫 Teacher Endpoints (Requires TEACHER Role)

### QR Code Generation & Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/teacher/generateQR` | Generate QR code for attendance |
| GET | `/teacher/session/{sessionId}/attendance` | Get attendance for QR session |

**Request Body (generateQR):**
```json
{
  "teacherId": 1,
  "teacherName": "Dr. CTeacher0",
  "subjectId": 21,
  "year": 1,
  "section": "A",
  "department": "Computer Science",
  "teacherLatitude": 28.5355,
  "teacherLongitude": 77.391,
  "qrExpiryTime": 10
}
```

**Response (generateQR):**
```json
{
  "sessionId": 5,
  "token": "ca667f0d-55be-4d20-96cd-f377bb30221d",
  "expiryTime": "2026-06-12T15:30:00",
  "qrImageBase64": "iVBORw0KGgoAAAANSUhEUgAAAvoAAA..."
}
```

---

## 👨‍🎓 Student Endpoints (Requires STUDENT Role)

### Attendance Marking & History
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/student/mark-attendance` | Mark attendance using QR code |
| GET | `/student/attendance-history` | Get student's attendance history |
| GET | `/student/attendance/me` | Get attendance summary by subject |

**Request Body (mark-attendance):**
```json
{
  "sessionId": 5,
  "token": "ca667f0d-55be-4d20-96cd-f377bb30221d",
  "studentLatitude": 28.5355,
  "studentLongitude": 77.391
}
```

**Response (mark-attendance):**
```json
"Attendance marked successfully ✅"
```

**Response (attendance-history):**
```json
[
  {
    "id": 1,
    "subjectName": "Data Structures",
    "teacherName": "Dr. CTeacher0",
    "date": "2026-06-12",
    "time": "15:25:30",
    "status": "PRESENT",
    "markedTime": "15:25:30"
  }
]
```

**Response (attendance/me):**
```json
{
  "subjectAttendance": [
    {
      "subject": "Data Structures",
      "percent": 85.5
    }
  ],
  "history": [
    {
      "subject": "Data Structures",
      "date": "2026-06-12",
      "time": "15:25:30",
      "status": "PRESENT"
    }
  ]
}
```

---

## 🔑 Authentication

### JWT Token Usage
Include the JWT token in the `Authorization` header for protected endpoints:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Test Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@gmail.com | admin123 |
| Teacher | teacher1@college.com | teacher123 |
| Student | student1@college.com | student123 |

---

## 🔄 Request/Response Patterns

### Success Response (200 OK)
```json
{
  "data": "success",
  "message": "Operation completed"
}
```

### Error Response (400/401/500)
```json
{
  "message": "Error description",
  "error": "ERROR_CODE"
}
```

---

## 📊 Data Models

### Teacher Object
```json
{
  "id": 1,
  "name": "Dr. CTeacher0",
  "email": "teacher1@college.com",
  "subjects": [...],
  "department": {...}
}
```

### Student Object
```json
{
  "id": 1,
  "name": "Student 1",
  "email": "student1@college.com",
  "rollNumber": "CS001",
  "section": {...}
}
```

### Subject Object
```json
{
  "id": 21,
  "name": "Data Structures",
  "year": 1,
  "section": {...},
  "department": {...},
  "teacher": {...}
}
```

### Department Object
```json
{
  "id": 1,
  "name": "Computer Science"
}
```

### Section Object
```json
{
  "id": 1,
  "sectionName": "A",
  "year": 1,
  "department": {...}
}
```

---

## 🛡️ Security & Authorization

| Role | Access |
|------|--------|
| **ADMIN** | Full access to all CRUD operations on departments, subjects, sections, teachers, students, and registration requests |
| **TEACHER** | Can generate QR codes, view session attendance, mark attendance (if needed) |
| **STUDENT** | Can mark attendance, view personal attendance history |

---

## 🔗 Related Links

- **Frontend**: https://qrlocbasedattendance.netlify.app/
- **Backend**: https://my-project-80ir.onrender.com
- **Health Check**: https://my-project-80ir.onrender.com/health

---

## 📝 Notes

- All dates are in `YYYY-MM-DD` format
- All times are in `HH:mm:ss` format (24-hour)
- Coordinates use decimal degrees (latitude, longitude)
- Distance validation uses Haversine formula with 50m default radius
- QR codes expire by default after 5 minutes (configurable in request)
- All endpoints except public ones require valid JWT token in Authorization header
