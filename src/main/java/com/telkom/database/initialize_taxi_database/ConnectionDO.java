package com.telkom.database;

public class ConnectionDO {
    public int id;
    public int from_direction_id;
    public int to_direction_id;
    public String notes;

    public ConnectionDO() {}

    public ConnectionDO(int fromDirectionId, int toDirectionId, String notes) {
        this.from_direction_id = fromDirectionId;
        this.to_direction_id = toDirectionId;
        this.notes = notes;
    }
}
