package com.hospital.servlet;

import com.hospital.dao.DBConnection;
import org.mindrot.jbcrypt.BCrypt;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;

@WebServlet("/api/seed")
public class SeedServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html><body style='font-family:monospace;padding:20px;background:#111;color:#0f0'>");
        out.println("<h2>🏥 Hospital DB Seeding</h2><pre>");

        try (Connection c = DBConnection.getConnection()) {

            // Create tables
            createTables(c, out);

            // Check if already seeded
            ResultSet rs = c.createStatement().executeQuery("SELECT COUNT(*) FROM users WHERE role='admin'");
            rs.next();
            if (rs.getInt(1) > 0) {
                out.println("⚠️  Database already seeded! Skipping...");
                out.println("\n<a href='/hospital/' style='color:#0af'>→ Go to App</a>");
                out.println("</pre></body></html>");
                return;
            }

            // Admin user
            String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users (name,email,password,phone,role) VALUES (?,?,?,?,?)");
            ps.setString(1, "Admin"); ps.setString(2, "admin@hospital.com");
            ps.setString(3, hash);   ps.setString(4, "9999999999");
            ps.setString(5, "admin");
            ps.executeUpdate();
            out.println("✅ Admin created: admin@hospital.com / admin123");

            // Doctors
            String[][] doctors = {
                {"Arun Sharma",  "arun@hospital.com",  "Cardiology",  "MBBS, MD (Cardiology)",  "12","9876500001","Expert in interventional cardiology and heart failure management.","800","4.8"},
                {"Priya Nair",   "priya@hospital.com", "Dermatology", "MBBS, MD (Dermatology)", "8", "9876500002","Specialist in cosmetic dermatology and skin disorder treatment.","600","4.7"},
                {"Rajesh Kumar","rajesh@hospital.com", "Orthopedics", "MBBS, MS (Ortho)",        "15","9876500003","Expert in joint replacement and sports injuries.","900","4.9"},
                {"Meena Reddy", "meena@hospital.com",  "Pediatrics",  "MBBS, MD (Pediatrics)",  "10","9876500004","Compassionate pediatrician specializing in child development.","500","4.9"},
                {"Suresh Patel","suresh@hospital.com", "General",     "MBBS, MD (General)",     "6", "9876500005","General health and preventive care specialist.","400","4.5"},
                {"Kavya Sharma","kavya@hospital.com",  "Neurology",   "MBBS, DM (Neurology)",   "9", "9876500006","Expert in neurological disorders and stroke management.","750","4.6"},
            };

            String[] times = {"09:00 AM","10:00 AM","11:00 AM","02:00 PM","03:00 PM","04:00 PM"};
            String docSql  = "INSERT INTO doctors (name,email,specialty,qualification,experience,phone,bio,consultation_fee,rating) VALUES (?,?,?,?,?,?,?,?,?)";
            String slotSql = "INSERT INTO slots (doctor_id,date,time) VALUES (?,?,?)";

            PreparedStatement docPs  = c.prepareStatement(docSql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement slotPs = c.prepareStatement(slotSql);

            for (String[] d : doctors) {
                docPs.setString(1, d[0]); docPs.setString(2, d[1]);
                docPs.setString(3, d[2]); docPs.setString(4, d[3]);
                docPs.setInt(5, Integer.parseInt(d[4]));
                docPs.setString(6, d[5]); docPs.setString(7, d[6]);
                docPs.setDouble(8, Double.parseDouble(d[7]));
                docPs.setDouble(9, Double.parseDouble(d[8]));
                docPs.executeUpdate();

                ResultSet keys = docPs.getGeneratedKeys();
                keys.next();
                int docId = keys.getInt(1);

                for (int day = 1; day <= 7; day++) {
                    String date = LocalDate.now().plusDays(day).toString();
                    for (String time : times) {
                        slotPs.setInt(1, docId);
                        slotPs.setString(2, date);
                        slotPs.setString(3, time);
                        slotPs.addBatch();
                    }
                }
                out.println("✅ Dr. " + d[0] + " added with 42 slots");
            }
            slotPs.executeBatch();

            out.println("\n🏥 Database seeded successfully!");
            out.println("\n<a href='/hospital/' style='color:#0af;font-size:16px'>→ Go to Hospital App</a>");

        } catch (Exception e) {
            out.println("❌ Error: " + e.getMessage());
            e.printStackTrace(out);
        }
        out.println("</pre></body></html>");
    }

    private void createTables(Connection c, PrintWriter out) throws SQLException {
        Statement st = c.createStatement();
        st.execute("CREATE TABLE IF NOT EXISTS users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, " +
            "email VARCHAR(100) NOT NULL UNIQUE, password VARCHAR(255) NOT NULL, " +
            "phone VARCHAR(20) NOT NULL, role ENUM('patient','admin') DEFAULT 'patient', " +
            "gender ENUM('male','female','other') DEFAULT NULL, " +
            "date_of_birth DATE DEFAULT NULL, address TEXT DEFAULT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        st.execute("CREATE TABLE IF NOT EXISTS doctors (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, " +
            "email VARCHAR(100) NOT NULL UNIQUE, " +
            "specialty ENUM('Cardiology','Dermatology','General','Neurology','Orthopedics','Pediatrics','Psychiatry','Radiology','Dentistry','Gynecology','ENT','Ophthalmology') NOT NULL, " +
            "qualification VARCHAR(150) NOT NULL, experience INT NOT NULL, " +
            "phone VARCHAR(20), bio TEXT, consultation_fee DECIMAL(10,2) NOT NULL, " +
            "rating FLOAT DEFAULT 4.0, is_active TINYINT(1) DEFAULT 1, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        st.execute("CREATE TABLE IF NOT EXISTS slots (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, doctor_id INT NOT NULL, " +
            "date DATE NOT NULL, time VARCHAR(20) NOT NULL, is_booked TINYINT(1) DEFAULT 0, " +
            "FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE)");

        st.execute("CREATE TABLE IF NOT EXISTS appointments (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, patient_id INT NOT NULL, " +
            "doctor_id INT NOT NULL, slot_id INT NOT NULL, " +
            "appointment_date DATE NOT NULL, appointment_time VARCHAR(20) NOT NULL, " +
            "reason TEXT NOT NULL, status ENUM('pending','confirmed','completed','cancelled') DEFAULT 'confirmed', " +
            "notes TEXT DEFAULT NULL, prescription TEXT DEFAULT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (patient_id) REFERENCES users(id), " +
            "FOREIGN KEY (doctor_id)  REFERENCES doctors(id), " +
            "FOREIGN KEY (slot_id)    REFERENCES slots(id))");

        out.println("✅ All tables created");
    }
}
