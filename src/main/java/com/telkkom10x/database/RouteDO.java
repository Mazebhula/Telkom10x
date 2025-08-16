package com.telkkom10x.database;

public class RouteDO {
    public int id;
    public int direction_id; // FK to directions.id
    public String streets;
    public String sign;

    public RouteDO() {}

    public RouteDO(int directionId, String streets, String sign) {
        this.direction_id = directionId;
        this.streets = streets;
        this.sign = sign;
    }
}
