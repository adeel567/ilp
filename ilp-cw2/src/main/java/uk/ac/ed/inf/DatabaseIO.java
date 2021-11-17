package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

/**
 * Stores common static methods and attributes for interacting with the database.
 */
public class DatabaseIO {
    private static final String DB_NAME = "derbyDB";
    private static final Config config = Config.getInstance();
    public static final String jdbcString = String.format("jdbc:derby://%s:%s/%s", config.getDbHost(),
            config.getDbPort(), DB_NAME);


    /**
     * Write the given orders completed to the deliveries table.
     * Existing table will be overwritten if it exists.
     * @param orders that shall be written to the table.
     */
    public static void writeDeliveriesTable(ArrayList<Order> orders) {
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null,null,
                    "DELIVERIES", null);
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
            System.err.println("Failed to write to database, table deliveries");
        }
    }

    /**
     * Write the flightpath into the Flightpath table.
     * Existing table will be overwritten if it exists.
     * @param dm collection of DroneMoves as the flightpath to write.
     */
    public static void writeFilepathDatabase(ArrayList<DroneMove> dm) {
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getTables(null,null,
                    "FLIGHTPATH", null);
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
            System.err.println("Failed to write to database, table flightpath");
        }
    }
}
