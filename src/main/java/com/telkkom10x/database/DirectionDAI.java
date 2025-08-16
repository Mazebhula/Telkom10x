package com.telkkom10x.database;

import net.lemnik.eodsql.*;

import java.util.List;

public interface DirectionDAI extends BaseQuery {

    @Select("SELECT * FROM directions WHERE id = ?{1}")
    DirectionsDO getDirectionById(int id);

    @Select("SELECT * FROM directions WHERE starting_position = ?{1} AND destination = ?{2}")
    DirectionsDO getDirectionByStartAndDestination(String start, String dest);

    @Select("SELECT * FROM directions")
    List<DirectionsDO> getAllDirections();

    @Update("INSERT INTO directions (starting_position, destination) VALUES (?{1.starting_position}, ?{1.destination})")
    void insertDirection(DirectionsDO directionsDO);

    @Update("DELETE FROM directions WHERE id = ?{1}")
    void deleteDirectionById(int id);
}
