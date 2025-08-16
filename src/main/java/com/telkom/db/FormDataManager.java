package com.telkom.db;

import java.sql.*;

public class FormDataManager {
    private Connection conn;

    public FormDataManager(String dbFileName) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
        createTable();
    }

    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS form_data (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT,
                form_name TEXT,
                field TEXT,
                encrypted_value TEXT,
                iv TEXT
            );
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveField(String userId, String formName, String field, String value) throws Exception {
        String[] encrypted = CryptoUtils.encrypt(value);
        String sql = "INSERT INTO form_data (user_id, form_name, field, encrypted_value, iv) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, formName);
            stmt.setString(3, field);
            stmt.setString(4, encrypted[0]); // ciphertext
            stmt.setString(5, encrypted[1]); // iv
            stmt.executeUpdate();
            System.out.println("Saved field: " + field);
        }
    }

    public void printDecryptedFields(String userId) throws Exception {
        String sql = "SELECT form_name, field, encrypted_value, iv FROM form_data WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String form = rs.getString("form_name");
                String field = rs.getString("field");
                String value = CryptoUtils.decrypt(rs.getString("encrypted_value"), rs.getString("iv"));
                System.out.printf("[%s] %s: %s%n", form, field, value);
            }
        }
    }
}
