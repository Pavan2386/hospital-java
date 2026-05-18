package com.hospital.dao;

import com.hospital.model.Doctor;
import java.sql.*;
import java.util.*;

public class DoctorDAO {

    public List<Doctor> findAll(String specialty, String search) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM doctors WHERE is_active=1");
        List<Object> params = new ArrayList<>();
        if (specialty != null && !specialty.isEmpty()) { sql.append(" AND specialty=?"); params.add(specialty); }
        if (search != null && !search.isEmpty())       { sql.append(" AND name LIKE ?"); params.add("%"+search+"%"); }
        sql.append(" ORDER BY name");

        List<Doctor> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i+1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Doctor findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM doctors WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Doctor d = mapRow(rs);
                d.setSlots(new SlotDAO().findAvailableByDoctor(id));
                return d;
            }
            return null;
        }
    }

    public Doctor create(Doctor d) throws SQLException {
        String sql = "INSERT INTO doctors(name,email,specialty,qualification,experience,phone,bio,consultation_fee,rating) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,d.getName()); ps.setString(2,d.getEmail()); ps.setString(3,d.getSpecialty());
            ps.setString(4,d.getQualification()); ps.setInt(5,d.getExperience()); ps.setString(6,d.getPhone());
            ps.setString(7,d.getBio()); ps.setDouble(8,d.getConsultationFee());
            ps.setDouble(9,d.getRating()>0?d.getRating():4.0);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) d.setId(rs.getInt(1));
            return d;
        }
    }

    public void update(Doctor d) throws SQLException {
        String sql = "UPDATE doctors SET name=?,specialty=?,qualification=?,experience=?,phone=?,bio=?,consultation_fee=?,rating=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,d.getName()); ps.setString(2,d.getSpecialty()); ps.setString(3,d.getQualification());
            ps.setInt(4,d.getExperience()); ps.setString(5,d.getPhone()); ps.setString(6,d.getBio());
            ps.setDouble(7,d.getConsultationFee()); ps.setDouble(8,d.getRating()); ps.setInt(9,d.getId());
            ps.executeUpdate();
        }
    }

    public void deactivate(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE doctors SET is_active=0 WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getInt("id")); d.setName(rs.getString("name")); d.setEmail(rs.getString("email"));
        d.setSpecialty(rs.getString("specialty")); d.setQualification(rs.getString("qualification"));
        d.setExperience(rs.getInt("experience")); d.setPhone(rs.getString("phone")); d.setBio(rs.getString("bio"));
        d.setConsultationFee(rs.getDouble("consultation_fee")); d.setRating(rs.getDouble("rating"));
        d.setActive(rs.getInt("is_active")==1);
        return d;
    }
}
