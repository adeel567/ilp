package uk.ac.ed.inf;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Set;

/**
 * Class for gathering and interacting with all of the day's orders.
 */
public class OrderHandler {

    /** Date of order's to obtain */
    private final LocalDate date;

    /** Orders of the day stored as a HashMap with their orderNo as key */
    private HashMap<String, Order> orders;

    /**
     * Obtains and initializes orders from database for a given date.
     *
     * @param dd   day of month
     * @param mm   month of year
     * @param yyyy year
     */
    public OrderHandler(int dd, int mm, int yyyy) {
        this.date = LocalDate.of(yyyy, mm, dd);
        System.out.println("\n" + date);
    }

    /**
     * Communicate with database to obtain the day's orders.
     * Then construct an order and initialise it with all of its
     * stops and estimated distance.
     */
    public void fetchOrders() {
        try {
            Connection conn = DriverManager.getConnection(DatabaseIO.getDBString());
            final String ordersQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psOrdersQuery = conn.prepareStatement(ordersQuery);
            psOrdersQuery.setString(1, date.format(DateTimeFormatter.ISO_LOCAL_DATE));

            HashMap<String, Order> ordersList = new HashMap<>();
            ResultSet rs = psOrdersQuery.executeQuery();
            while (rs.next()) {
                String orderNo = rs.getString("orderNo");
                String customer = rs.getString("customer");
                String deliverTo = rs.getString("deliverTo");
                var order = new Order(orderNo, customer, deliverTo);
                ordersList.put(order.getOrderNo(), order);
            }
            this.orders = ordersList;
        } catch (SQLException throwables) {
            System.err.println("Error in accessing database");
            System.exit(1);
        }

        if (this.orders.size() <= 0) {
            System.err.println("Warning: no orders parsed");
            System.exit(1);
        }
        System.out.println("TOTAL ORIGINAL COST: " + getTotalValue());
    }

    /**
     * Calculate the total value of all orders in the OrderHandler.
     *
     * @return the total value of all orders
     */
    public int getTotalValue() {
        int i = 0;
        for (Order order : orders.values()) {
            i += order.getDeliveryCost();
        }
        return i;
    }

    public HashMap<String, Order> getAllOrders() {
        return this.orders;
    }

    public Order get(String orderNo) {
        return this.orders.get(orderNo);
    }

    public Set<String> getAllOrderNos() {
        return this.orders.keySet();
    }

    public LocalDate getDate() {
        return this.date;
    }

}
