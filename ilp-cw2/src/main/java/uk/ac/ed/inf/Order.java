package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Order {
    public String orderNo;
    public ArrayList<String> orderItems;
    public int totalMovesUsed;
    public int totalCost;
    public ArrayList<Stop> stops;
    public LongLat destination;
    public String customer;

    private final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";
    private final Menus myMenu = new Menus("localhost","9898");
    private final Mapping myMapping = new Mapping("localhost", "9898");

    public Order(String orderNo, String customer, String deliverTo) {
        this.customer = customer;
        this.orderNo = orderNo;
        this.destination = new What3Words(deliverTo).getCoordinates();
        this.orderItems = fetchOrderItems();
        this.stops = generateInternalRoute();
        this.totalCost = myMenu.getDeliveryCost(this.orderItems.toArray(new String[orderItems.size()]));
    }

    private ArrayList<Stop> generateInternalRoute() {
        var unorderedShops = myMenu.getDeliveryStops(this.orderItems.toArray(new String[orderItems.size()]));

        ArrayList<Stop> allStops = new ArrayList<>();
        for (Shop shop : unorderedShops) {
            var coords = new What3Words(shop.location).coordinates;
            var s = new Stop(shop.name,coords);
            allStops.add(s);
        }
        allStops.add(new Stop(this.customer,this.destination));

        ArrayList<Stop> orderedStops = new ArrayList<>();
        if (allStops.size() > 2) {
            var p01 = myMapping.getRoute(allStops.get(0).coordinates, allStops.get(1).coordinates);
            var p12 = myMapping.getRoute(allStops.get(1).coordinates, allStops.get(2).coordinates);
            var dist01 = myMapping.getNumberOfMovesOfRoute(p01);
            var dist12 = myMapping.getNumberOfMovesOfRoute(p12);
            var dist012 =  dist01+ + dist12;

            var p10 = myMapping.getRoute(allStops.get(1).coordinates, allStops.get(0).coordinates);
            var p02 = myMapping.getRoute(allStops.get(0).coordinates, allStops.get(2).coordinates);
            var dist10 = myMapping.getNumberOfMovesOfRoute(p10);
            var dist02 = myMapping.getNumberOfMovesOfRoute(p02);
            var dist102 =  dist10+ + dist02;

            if (dist012 <= dist102) {
                allStops.get(0).distanceTo = 0;
                orderedStops.add(allStops.get(0));

                allStops.get(1).distanceTo = dist01;
                allStops.get(1).routeTo = p01;
                orderedStops.add(allStops.get(1));

                allStops.get(2).distanceTo = dist12;
                allStops.get(2).routeTo = p12;
                orderedStops.add(allStops.get(2));
                this.totalMovesUsed = dist012;

            } else {
                allStops.get(1).distanceTo = 0;
                orderedStops.add(allStops.get(1));

                allStops.get(0).distanceTo = dist10;
                allStops.get(0).routeTo = p10;
                orderedStops.add(allStops.get(0));

                allStops.get(2).distanceTo = dist02;
                allStops.get(2).routeTo = p12;
                orderedStops.add(allStops.get(2));
                this.totalMovesUsed = dist102;

            }
        } else {
//            System.out.println(allStops.get(0).location.latitude);
//            System.out.println(allStops.get(1).location.latitude);
//
//            System.out.println(allStops.get(0).location.longitude);
//            System.out.println(allStops.get(0).location.latitude);
//            System.out.println("ss");
//            System.out.println(allStops.get(1).location.longitude);
//            System.out.println(allStops.get(1).location.latitude);
            var p01 = myMapping.getRoute(allStops.get(0).coordinates, allStops.get(1).coordinates);
            var dist01 = myMapping.getNumberOfMovesOfRoute(p01);

            allStops.get(0).distanceTo = 0;
            orderedStops.add(allStops.get(0));

            allStops.get(1).distanceTo = dist01;
            allStops.get(1).routeTo = p01;
            orderedStops.add(allStops.get(1));

            this.totalMovesUsed = dist01;
        }
        return orderedStops;
//        return allStops;
    }

    private ArrayList<String> fetchOrderItems(){
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            final String itemsQuery = "select * from orderDetails where orderNo=(?)";
            PreparedStatement psItemsQuery = conn.prepareStatement(itemsQuery);
            psItemsQuery.setString(1,this.orderNo);

            ArrayList<String> itemsList = new ArrayList<>();
            ResultSet rs = psItemsQuery.executeQuery();
            while(rs.next()) {
                String item = rs.getString("item");
                itemsList.add(item);
            }
            return itemsList;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public LongLat getStart() {
        return this.stops.get(0).coordinates;
    }

    public LongLat getDestination() {
        return this.destination;
    }

    public ArrayList<Stop> getAllStops() { return this.stops;}







}
