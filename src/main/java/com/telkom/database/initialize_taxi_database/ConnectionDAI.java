package com.telkom.database;

import net.lemnik.eodsql.*;

import java.util.List;

public interface ConnectionDAI extends BaseQuery {

    @Select("SELECT * FROM connections WHERE from_direction_id = ?{1}")
    List<com.telkom.database.ConnectionDO> getConnectionsFromDirection(int fromDirectionId);

    @Select("SELECT * FROM connections")
    List<com.telkom.database.ConnectionDO> getAllConnections();

    @Update("INSERT INTO connections (from_direction_id, to_direction_id, notes) " +
            "VALUES (?{1.from_direction_id}, ?{1.to_direction_id}, ?{1.notes})")
    void insertConnection(com.telkom.database.ConnectionDO connection);

    @Update("DELETE FROM connections WHERE id = ?{1}")
    void deleteConnection(int id);
}
