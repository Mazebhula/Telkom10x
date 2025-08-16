package com.telkkom10x;

import com.telkkom10x.database.*;
import com.telkkom10x.database.initialize_taxi_database.StopDAI;
import com.telkkom10x.database.initialize_taxi_database.StopDO;
import net.lemnik.eodsql.QueryTool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@SpringBootApplication
public class Telkkom10xApplication {
    public static void main(String[] args) {
        DatabaseIntializer.initialize();


//        /// /////////////////////////////////////////////////////////////////
//        Connection connection = null;
//        try {
//            connection = DriverManager.getConnection("jdbc:sqlite:8ta-xi.sqlite");
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Create DAO for stops
//        StopDAI stopDAI = QueryTool.getQuery(connection, StopDAI.class);
//        DirectionDAI directionDAI=QueryTool.getQuery(connection,DirectionDAI.class);
//        PossibleTaxiDAI possibleTaxiDAI=QueryTool.getQuery(connection,PossibleTaxiDAI.class);
//// Step 1: Create direction MTN -> Tshepisong
//
//
//        DirectionsDO dirMTNTship = new DirectionsDO("Johannesburg", "Tshepisong");
//        directionDAI.insertDirection(dirMTNTship);
//        DirectionsDO mtnTship = directionDAI.getDirectionByStartAndDestination("Johannesburg", "Tshepisong");
//
//// Step 2: Insert possible taxi info
//        PossibleTaxiDO taxiMTNTship = new PossibleTaxiDO(mtnTship.id, "MTN Taxi Rank", "rank");
//
//        possibleTaxiDAI.insertTaxi(taxiMTNTship);
//
//// Step 3: Insert stops in order
//        stopDAI.insertStop(new StopDO(mtnTship.id, 1, "MTN Taxi Rank"));
//        stopDAI.insertStop(new StopDO(mtnTship.id, 2, "Bree Taxi Rank"));
//        stopDAI.insertStop(new StopDO(mtnTship.id, 3, "Main Reef Rd"));
//        stopDAI.insertStop(new StopDO(mtnTship.id, 4, "Tshepisong"));
//
//// Step 4: Insert route info (optional)\
//        RouteDAI routeDAI=QueryTool.getQuery(connection,RouteDAI.class);
//        RouteDO routeMTNTship = new RouteDO(mtnTship.id, "Main Reef Rd", "7");
//        routeDAI.insertRoute(routeMTNTship);
//
//
//
//
//        DirectionsDO dirMTNTship1 = new DirectionsDO("Newtown", "Randburg");
//        directionDAI.insertDirection(dirMTNTship1);
//        DirectionsDO mtnTship1 = directionDAI.getDirectionByStartAndDestination("Newtown", "Randburg");
//
//// Step 2: Insert possible taxi info
//        PossibleTaxiDO taxiMTNTship1 = new PossibleTaxiDO(mtnTship.id, "MTN Taxi Rank", "rank");
//
//        possibleTaxiDAI.insertTaxi(taxiMTNTship1);
//
//// Step 3: Insert stops in order
//        stopDAI.insertStop(new StopDO(mtnTship.id, 1, "Braamfontein"));
//        stopDAI.insertStop(new StopDO(mtnTship.id, 2, "Hydepark"));
//        stopDAI.insertStop(new StopDO(mtnTship.id, 3, "Rosesbank"));
//        stopDAI.insertStop(new StopDO(mtnTship.id, 4, "Car tracker"));
//
//// Step 4: Insert route info (optional)\
//        //RouteDAI routeDAI=QueryTool.getQuery(connection,RouteDAI.class);
//        RouteDO routeMTNTship1 = new RouteDO(mtnTship.id, "Jan smuts Rd", "7");
//        routeDAI.insertRoute(routeMTNTship1);




        /// //////////////////////////////////////////////////////////////////////////////////


        SpringApplication.run(Telkkom10xApplication.class, args);
    }
}