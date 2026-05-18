package com.hospital.dao;

import com.hospital.model.Slot;
import java.sql.*;
import java.util.*;

public class SlotDAO {

    public List<Slot> findAvailableByDoctor(int doctorId) throws SQLException {
        String sql = "SELECT * FROM slots WHERE doctor_id=? AND is_booked=0 AND date>=CURDATE() ORDER BY date,time";
        List<Slot> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Slot findAvailableSlot(int doctorId, int slotId) throws SQLException {
        String sql = "SELECT * FROM slots WHERE id=? AND doctor_id=? AND is_booked=0";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1,slotId); ps.setInt(2,doctorId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public void markBooked(int slotId, boolean booked) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE slots SET is_booked=? WHERE id=?")) {
            ps.setInt(1,booked?1:0); ps.setInt(2,slotId); ps.executeUpdate();
        }
    }

    public void addSlots(int doctorId, List<Slot> slots) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO slots(doctor_id,date,time) VALUES(?,?,?)")) {
            for (Slot s : slots) {
                ps.setInt(1,doctorId); ps.setString(2,s.getDate()); ps.setString(3,s.getTime()); ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private Slot mapRow(ResultSet rs) throws SQLException {
        Slot s = new Slot();
        s.setId(rs.getInt("id")); s.setDoctorId(rs.getInt("doctor_id"));
        s.setDate(rs.getString("date")); s.setTime(rs.getString("time"));
        s.setBooked(rs.getInt("is_booked")==1);
        return s;
    }
}
