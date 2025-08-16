package com.telkkom10x.database;

import net.lemnik.eodsql.*;

import java.util.List;

public interface RouteDAI extends BaseQuery {

    @Select("SELECT * FROM routes WHERE id = ?{1}")
    RouteDO getRouteById(int id);

    @Select("SELECT * FROM routes WHERE direction_id = ?{1}")
    List<RouteDO> getRoutesByDirectionId(int directionId);

    @Select("SELECT * FROM routes")
    List<RouteDO> getAllRoutes();

    @Update("INSERT INTO routes (direction_id, streets, sign) " +
            "VALUES (?{1.direction_id}, ?{1.streets}, ?{1.sign})")
    void insertRoute(RouteDO route);

    @Update("DELETE FROM routes WHERE id = ?{1}")
    void deleteRoute(int id);
}
