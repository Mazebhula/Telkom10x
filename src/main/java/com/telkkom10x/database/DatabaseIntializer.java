package com.telkkom10x.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class
DatabaseIntializer {
    private static final String DB_URL = "jdbc:sqlite:8ta-xi.sqlite";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("CREATE TABLE IF NOT EXISTS directions (" +
                    "starting_position TEXT PRIMARY KEY, " +
                    "destination TEXT NOT NULL)");

            System.out.println("Database schema initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

}
