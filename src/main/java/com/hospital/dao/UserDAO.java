package com.hospital.dao;

import com.hospital.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.*;

public class UserDAO {

    public User create(User u) throws SQLException {
        String sql = "INSERT INTO users(name,email,password,phone,role,gender,date_of_birth,address) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, BCrypt.hashpw(u.getPassword(), BCrypt.gensalt()));
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getRole() != null ? u.getRole() : "patient");
            ps.setString(6, u.getGender());
            ps.setString(7, u.getDateOfBirth());
            ps.setString(8, u.getAddress());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) u.setId(rs.getInt(1));
            u.setPassword(null);
            return u;
        }
    }

    public User findByEmail(String email) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE email=?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public User findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { User u = mapRow(rs); u.setPassword(null); return u; }
            return null;
        }
    }

    public boolean emailExists(String email) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id FROM users WHERE email=?")) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        }
    }

    public void update(User u) throws SQLException {
        String sql = "UPDATE users SET name=?,phone=?,gender=?,date_of_birth=?,address=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,u.getName()); ps.setString(2,u.getPhone());
            ps.setString(3,u.getGender()); ps.setString(4,u.getDateOfBirth());
            ps.setString(5,u.getAddress()); ps.setInt(6,u.getId());
            ps.executeUpdate();
        }
    }

    public List<User> findAllPatients() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE role='patient' ORDER BY created_at DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { User u = mapRow(rs); u.setPassword(null); list.add(u); }
        }
        return list;
    }

    public boolean checkPassword(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id")); u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email")); u.setPassword(rs.getString("password"));
        u.setPhone(rs.getString("phone")); u.setRole(rs.getString("role"));
        u.setGender(rs.getString("gender")); u.setDateOfBirth(rs.getString("date_of_birth"));
        u.setAddress(rs.getString("address")); u.setCreatedAt(rs.getString("created_at"));
        return u;
    }
}
