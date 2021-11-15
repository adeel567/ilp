package uk.ac.ed.inf;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderHandler {

    private final LocalDate date;
    private HashMap<String,Order> orders;

    private final Menus myMenu = Menus.getInstance();
    private final Navigation myNavigation = Navigation.getInstance();


    public OrderHandler(int dd, int mm, int yyyy) {
        this.date = LocalDate.of(yyyy,mm,dd);
        System.out.println(date);
    }

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

    public Order getOrder(String orderNo) {
        return this.orders.get(orderNo);
    }

    public LocalDate getDate() {
        return this.date;
    }

}
