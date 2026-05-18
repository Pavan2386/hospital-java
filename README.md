# 🏥 Hospital Appointment System — Java Servlets + MySQL

## Tech Stack
- **Backend:** Java Servlets (javax.servlet)
- **Database:** MySQL via XAMPP
- **Server:** Apache Tomcat (via Eclipse)
- **Frontend:** HTML5 + CSS3 + JavaScript (Fetch API)
- **Build:** Maven

## Setup Steps

### 1. Start XAMPP
- Start Apache + MySQL
- Go to phpMyAdmin → Create database: `hospital_db`

### 2. Import into Eclipse
- Open Eclipse → File → Import → Maven → Existing Maven Projects
- Browse to this folder → Finish

### 3. Add Tomcat Server in Eclipse
- Window → Preferences → Server → Runtime Environments → Add
- Choose Apache Tomcat 9.0 → Next → Browse to Tomcat folder → Finish

### 4. Run the Project
- Right-click project → Run As → Run on Server → Tomcat
- App opens at: http://localhost:8080/hospital/

### 5. Seed the Database
- Visit: http://localhost:8080/hospital/api/seed
- This auto-creates all tables and inserts sample data

## Login Credentials
| Role  | Email               | Password  |
|-------|---------------------|-----------|
| Admin | admin@hospital.com  | admin123  |

## API Endpoints
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/auth/login | Login |
| POST | /api/auth/register | Register |
| POST | /api/auth/logout | Logout |
| GET | /api/doctors | List doctors |
| GET | /api/doctors/{id} | Doctor + slots |
| POST | /api/appointments | Book appointment |
| GET | /api/appointments/my | My appointments |
| PUT | /api/appointments/{id}/cancel | Cancel |
| GET | /api/admin/appointments | All (Admin) |
| GET | /api/admin/users | All patients (Admin) |

## Syllabus Coverage
| Exercise | Feature |
|----------|---------|
| Ex. 5 | JDBC + MySQL CRUD |
| Ex. 7 | Servlet + Database integration |
| Ex. 8 | HTTP Sessions + Cookies |
| Ex. 1-2 | HTML + CSS + JavaScript frontend |
