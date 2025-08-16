package com.telkkom10x.database;

public class PossibleTaxiDO {
    public int id;
    public int direction_id; // FK to directions.id
    public String location;
    public String type;      // e.g., "hand sign", "rank", etc.

    public PossibleTaxiDO() {}

    public PossibleTaxiDO(int directionId, String location, String type) {
        this.direction_id = directionId;
        this.location = location;
        this.type = type;
    }
}
