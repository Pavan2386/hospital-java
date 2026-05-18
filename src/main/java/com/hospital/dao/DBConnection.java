package com.hospital.dao;
import java.sql.*;

public class DBConnection {

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Check if running on Railway (environment variables exist)
            String host = System.getenv("MYSQLHOST");

            if (host != null) {
                // ── Running on Railway ──
                String port     = System.getenv("MYSQLPORT");
                String database = System.getenv("MYSQLDATABASE");
                String user     = System.getenv("MYSQLUSER");
                String password = System.getenv("MYSQLPASSWORD");

                String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                           + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

                return DriverManager.getConnection(url, user, password);

            } else {
                // ── Running on Localhost (XAMPP) ──
                String url  = "jdbc:mysql://localhost:3306/hospital_db"
                            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                return DriverManager.getConnection(url, "root", "");
            }

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found: " + e.getMessage());
        }
    }
}