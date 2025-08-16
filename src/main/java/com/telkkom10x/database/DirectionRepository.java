package com.telkkom10x.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DirectionRepository {
    private static final String DB_URL = "jdbc:sqlite:8ta-xi.sqlite";

    public static void addDirection(String startingPosition, String destination) {
        String sql = "INSERT INTO directions(starting_position, destination) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, startingPosition);
            pstmt.setString(2, destination);

            pstmt.executeUpdate();
            System.out.println("Inserted: " + startingPosition + " -> " + destination);

        } catch (SQLException e) {
            System.err.println("Error inserting into directions: " + e.getMessage());
        }
    }
}
