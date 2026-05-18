package com.hospital.dao;

import com.hospital.model.Appointment;
import java.sql.*;
import java.util.*;

public class AppointmentDAO {

    public Appointment create(Appointment a) throws SQLException {
        String sql = "INSERT INTO appointments(patient_id,doctor_id,slot_id,appointment_date,appointment_time,reason,status) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,a.getPatientId()); ps.setInt(2,a.getDoctorId()); ps.setInt(3,a.getSlotId());
            ps.setString(4,a.getAppointmentDate()); ps.setString(5,a.getAppointmentTime());
            ps.setString(6,a.getReason()); ps.setString(7,"confirmed");
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) a.setId(rs.getInt(1));
            return a;
        }
    }

    public List<Appointment> findByPatient(int patientId) throws SQLException {
        String sql = "SELECT a.*,d.name AS doctor_name,d.specialty,d.consultation_fee " +
                     "FROM appointments a JOIN doctors d ON a.doctor_id=d.id WHERE a.patient_id=? ORDER BY a.appointment_date DESC";
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs, true));
        }
        return list;
    }

    public List<Appointment> findAll() throws SQLException {
        String sql = "SELECT a.*,u.name AS patient_name,u.email AS patient_email,u.phone AS patient_phone," +
                     "d.name AS doctor_name,d.specialty,d.consultation_fee " +
                     "FROM appointments a JOIN users u ON a.patient_id=u.id JOIN doctors d ON a.doctor_id=d.id ORDER BY a.created_at DESC";
        List<Appointment> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment a = mapRow(rs, true);
                try { a.setPatientName(rs.getString("patient_name")); } catch (Exception ignored) {}
                try { a.setPatientEmail(rs.getString("patient_email")); } catch (Exception ignored) {}
                try { a.setPatientPhone(rs.getString("patient_phone")); } catch (Exception ignored) {}
                list.add(a);
            }
        }
        return list;
    }

    public Appointment findById(int id) throws SQLException {
        String sql = "SELECT a.*,d.name AS doctor_name,d.specialty,d.consultation_fee " +
                     "FROM appointments a JOIN doctors d ON a.doctor_id=d.id WHERE a.id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs, true) : null;
        }
    }

    public void updateStatus(int id, String status, String notes, String prescription) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE appointments SET status=?,notes=?,prescription=? WHERE id=?")) {
            ps.setString(1,status); ps.setString(2,notes); ps.setString(3,prescription); ps.setInt(4,id);
            ps.executeUpdate();
        }
    }

    public void cancel(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE appointments SET status='cancelled' WHERE id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }

    private Appointment mapRow(ResultSet rs, boolean withJoins) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id")); a.setPatientId(rs.getInt("patient_id")); a.setDoctorId(rs.getInt("doctor_id"));
        a.setSlotId(rs.getInt("slot_id")); a.setAppointmentDate(rs.getString("appointment_date"));
        a.setAppointmentTime(rs.getString("appointment_time")); a.setReason(rs.getString("reason"));
        a.setStatus(rs.getString("status")); a.setNotes(rs.getString("notes")); a.setPrescription(rs.getString("prescription"));
        a.setCreatedAt(rs.getString("created_at"));
        if (withJoins) {
            try { a.setDoctorName(rs.getString("doctor_name")); }       catch (Exception ignored) {}
            try { a.setDoctorSpecialty(rs.getString("specialty")); }     catch (Exception ignored) {}
            try { a.setConsultationFee(rs.getDouble("consultation_fee")); } catch (Exception ignored) {}
        }
        return a;
    }
}
