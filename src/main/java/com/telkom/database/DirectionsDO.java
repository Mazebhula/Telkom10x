package com.telkom.database;

public class DirectionsDO {
    public int id;
    public String starting_position;
    public String destination;

    public DirectionsDO() {}

    public DirectionsDO(String start, String dest) {
        this.starting_position = start;
        this.destination = dest;
    }
}
