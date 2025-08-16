package com.telkom.database.initialize_taxi_database;

import net.lemnik.eodsql.*;
import java.util.List;

public interface StopDAI extends BaseQuery {

    @Select("SELECT * FROM stops WHERE direction_id = ?{1} ORDER BY stop_order ASC")
    List<StopDO> getStopsByDirectionId(int directionId);

    @Select("""
        SELECT * FROM stops 
        WHERE direction_id = ?{1} 
        AND stop_name = ?{2}
    """)
    StopDO getStopByDirectionAndName(int directionId, String stopName);

    @Update("INSERT INTO stops (direction_id, stop_order, stop_name) VALUES (?{1.direction_id}, ?{1.stop_order}, ?{1.stop_name})")
    void insertStop(StopDO stop);

    @Update("DELETE FROM stops WHERE id = ?{1}")
    void deleteStop(int id);
}
