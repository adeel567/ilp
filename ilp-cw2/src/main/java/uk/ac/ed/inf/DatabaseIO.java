package uk.ac.ed.inf;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseIO {
    private static final String DB_NAME = "derbyDB";
    private static final Config config = Config.getInstance();
    public static final String jdbcString = String.format("jdbc:derby://%s:%s/%s", config.getDbHost(),
            config.getDbPort(), DB_NAME);


    public static void writeDeliveriesTable(ArrayList<Order> orders) {
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null,null,
                    "deliveries", null);
            if (resultSet.next()) {
                statement.execute("drop table deliveries");
            }
            statement.execute(
                    "create table deliveries(" +
                            "orderNo char(8), " +
                            "deliveredTo varchar(19), " +
                            "costInPence int)"
            );

            PreparedStatement psDeliveries = conn.prepareStatement(
                    "insert into deliveries values (?,?,?)");
            for (Order order : orders) {
                psDeliveries.setString(1,order.getOrderNo());
                psDeliveries.setString(2,order.getDestinationW3W());
                psDeliveries.setInt(3,order.getDeliveryCost());
                psDeliveries.execute();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void writeFilepathDatabase(ArrayList<DroneMove> dm) {
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null,null,
                    "flightpath", null);
            if (resultSet.next()) {
                statement.execute("drop table flightpath");
            }
            statement.execute(
                    "create table flightpath(" +
                            "orderNo char(8), " +
                            "fromLongitude double," +
                            "fromLatitude double," +
                            "angle integer," +
                            "toLongitude double," +
                            "toLatitude double)"
            );

            PreparedStatement psFlightpath = conn.prepareStatement(
                    "insert into flightpath values (?,?,?,?,?,?)");
            for (DroneMove droneMove : dm) {
                psFlightpath.setString(1,droneMove.getId());
                psFlightpath.setDouble(2,droneMove.getFrom().longitude);
                psFlightpath.setDouble(3,droneMove.getFrom().latitude);
                psFlightpath.setInt(4,droneMove.getAngle());
                psFlightpath.setDouble(5,droneMove.getTo().longitude);
                psFlightpath.setDouble(6,droneMove.getTo().latitude);
                psFlightpath.execute();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
