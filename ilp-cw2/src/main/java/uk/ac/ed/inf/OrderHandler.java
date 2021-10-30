package uk.ac.ed.inf;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class OrderHandler {

    private String date;
    private HashMap<String,Order> orders;

    private final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";
    private final Menus myMenu = new Menus("localhost","9898");
    private final Mapping myMapping = new Mapping("localhost", "9898");


    public OrderHandler(String date) {
       // this.date = new GregorianCalendar(year,month,day).getTime();
        //System.out.println(String.valueOf(date));
        this.date = date;
    }

    public void fetchOrders() {
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            final String ordersQuery = "select * from orders where deliveryDate=(?)";
            PreparedStatement psOrdersQuery = conn.prepareStatement(ordersQuery);
            psOrdersQuery.setString(1, date);

            HashMap<String,Order> ordersList = new HashMap<>();
            ResultSet rs = psOrdersQuery.executeQuery();
            while(rs.next()) {
                String orderNo = rs.getString("orderNo");
                String customer = rs.getString("customer");
                String deliverTo = rs.getString("deliverTo");
//                System.out.println("NEXT ORDER!!!!!!!!");
//                System.out.println(orderNo);
//                System.out.println(deliverTo);
                var order = new Order(orderNo,customer,deliverTo);
                ordersList.put(order.orderNo,order);
            }
            this.orders = ordersList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public HashMap<String,Order> getOrders(){
        return this.orders;
    }
}
