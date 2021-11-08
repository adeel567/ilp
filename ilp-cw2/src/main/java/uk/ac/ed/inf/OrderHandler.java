package uk.ac.ed.inf;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class OrderHandler {

    private final LocalDate date;
    private HashMap<String,Order> orders;

    private final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";
    private final Menus myMenu = new Menus("localhost","9898");
    private final Mapping myMapping = new Mapping("localhost", "9898");


    public OrderHandler(int dd, int mm, int yyyy) {
        this.date = LocalDate.of(yyyy,mm,dd);
    }

    public void fetchOrders() {
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
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
            throwables.printStackTrace();
        }
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
