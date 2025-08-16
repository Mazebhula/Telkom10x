package com.telkkom10x.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseIntializer {

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:8ta-xi.sqlite");
             Statement stmt = conn.createStatement()) {

            // Existing tables...

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS directions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    starting_position TEXT NOT NULL,
                    destination TEXT NOT NULL
                );
            """);
            stmt.executeUpdate("""
    CREATE TABLE IF NOT EXISTS stops (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        direction_id INTEGER NOT NULL,
        stop_order INTEGER NOT NULL,
        stop_name TEXT NOT NULL,
        FOREIGN KEY(direction_id) REFERENCES directions(id)
    );
""");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS possible_taxis (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    direction_id INTEGER NOT NULL,
                    location TEXT NOT NULL,
                    type TEXT NOT NULL,
                    FOREIGN KEY(direction_id) REFERENCES directions(id)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS routes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    direction_id INTEGER NOT NULL,
                    streets TEXT NOT NULL,
                    sign TEXT NOT NULL,
                    FOREIGN KEY(direction_id) REFERENCES directions(id)
                );
            """);

            // NEW table for multi-leg trips
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS connections (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    from_direction_id INTEGER NOT NULL,
                    to_direction_id INTEGER NOT NULL,
                    notes TEXT,
                    FOREIGN KEY(from_direction_id) REFERENCES directions(id),
                    FOREIGN KEY(to_direction_id) REFERENCES directions(id)
                );
            """);

        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }
}
