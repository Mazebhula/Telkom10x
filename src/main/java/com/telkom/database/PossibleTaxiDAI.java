package com.telkom.database;

import net.lemnik.eodsql.*;

import java.util.List;

public interface PossibleTaxiDAI extends BaseQuery {

    @Select("SELECT * FROM possible_taxis WHERE id = ?{1}")
    PossibleTaxiDO getTaxiById(int id);

    @Select("SELECT * FROM possible_taxis WHERE direction_id = ?{1}")
    List<PossibleTaxiDO> getTaxisByDirectionId(int directionId);

    @Select("SELECT * FROM possible_taxis")
    List<PossibleTaxiDO> getAllTaxis();

    @Update("INSERT INTO possible_taxis (direction_id, location, type) " +
            "VALUES (?{1.direction_id}, ?{1.location}, ?{1.type})")
    void insertTaxi(PossibleTaxiDO taxi);

    @Update("DELETE FROM possible_taxis WHERE id = ?{1}")
    void deleteTaxi(int id);
}
