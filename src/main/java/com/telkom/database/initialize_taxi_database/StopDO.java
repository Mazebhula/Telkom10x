package com.telkom.database.initialize_taxi_database;

public class StopDO {
    public int id;
    public int direction_id;
    public int stop_order;
    public String stop_name;

    public StopDO() {}

    public StopDO(int directionId, int stopOrder, String stopName) {
        this.direction_id = directionId;
        this.stop_order = stopOrder;
        this.stop_name = stopName;
    }
}
