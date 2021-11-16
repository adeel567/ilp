package uk.ac.ed.inf;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class for gathering and interacting with alll of the day's orders
 */
public class OrderHandler {

    private final LocalDate date;
    private HashMap<String,Order> orders;

    private final Menus myMenu = Menus.getInstance();
    private final NoFlyZones myNoFlyZones = NoFlyZones.getInstance();

    /**
     * Obtains and initializes orders from database for a given date.
     * @param dd day of month
     * @param mm month of year
     * @param yyyy year
     */
    public OrderHandler(int dd, int mm, int yyyy) {
        this.date = LocalDate.of(yyyy,mm,dd);
        System.out.println("\n" + date);
    }

    /**
     * Communicate with database to obtain the day's orders.
     * Then construct an order and initialise it with all of its
     * stops and estimated distance.
     */
    public void fetchOrders() {
        try {
            Connection conn = DriverManager.getConnection(DatabaseIO.jdbcString);
            final String ordersQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psOrdersQuery = conn.prepareStatement(ordersQuery);
            psOrdersQuery.setString(1, date.format(DateTimeFormatter.ISO_LOCAL_DATE));

            HashMap<String,Order> ordersList = new HashMap<>();
            ResultSet rs = psOrdersQuery.executeQuery();
            while(rs.next()) {
                String orderNo = rs.getString("orderNo");
                String customer = rs.getString("customer");
                String deliverTo = rs.getString("deliverTo");
                var order = new Order(orderNo,customer,deliverTo);
                ordersList.put(order.getOrderNo(),order);
            }
            this.orders = ordersList;
        } catch (SQLException throwables) {
            System.err.println("Error in accessing database");
        }
        assert (this.orders.size() > 0) : "Warning: no orders available";
    }


    public HashMap<String,Order> getAllOrders(){
        return this.orders;
    }

    public Order get(String orderNo) {
        return this.orders.get(orderNo);
    }

    public Set<String> getAllOrderNos(){
        return this.orders.keySet();
    }

    public LocalDate getDate() {
        return this.date;
    }

}
