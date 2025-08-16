package com.telkkom10x.database;

import net.lemnik.eodsql.BaseQuery;
import net.lemnik.eodsql.QueryTool;
import net.lemnik.eodsql.*;

import java.util.List;

public interface DirectionDAI extends BaseQuery {


    @Select("SELECT * FROM world WHERE name = ?{1}")
    DirectionsDO getWorldByName(String name);


    @Select("SELECT * FROM world")
    List<DirectionsDO> getAllWorlds();


    @Update("INSERT INTO world (name, width, height) VALUES (?{1.name}, ?{1.width}, ?{1.height})")
    void insertWorld(DirectionsDO directionsDO);


    @Update("UPDATE world SET width = ?{1.width}, height = ?{1.height} WHERE name = ?{1.name}")
    void updateWorld(DirectionsDO directionsDO);


    @Update("DELETE FROM world WHERE name = ?{1}")
    void deleteWorld(String name);

}