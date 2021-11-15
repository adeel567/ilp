package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Order {
    private String orderNo;
    private ArrayList<String> orderItems;
    private int deliveryCost;
    private ArrayList<Stop> allStops;
    private LongLat destination;
    private String customer;
    private String What3Words;
    private double estimatedDistance;

    private final Menus myMenu = Menus.getInstance();

    public Order(String orderNo, String customer, String deliverTo) {
        this.customer = customer;
        this.orderNo = orderNo;
        this.What3Words = deliverTo;
        this.destination = new What3Words(deliverTo).getCoordinates();
        this.orderItems = fetchOrderItems(orderNo);
        this.allStops = getStops();
        this.estimatedDistance = calcEstimatedDistance();
        this.deliveryCost = myMenu.getDeliveryCost(this.orderItems.toArray(new String[orderItems.size()]));
    }

    private Double calcEstimatedDistance() {
        assert allStops.size() >=2 : "only one stop on entire journey";

        //if there are two pickups, then try arranging so that the last pickup is closest to destination.
        if (allStops.size() == 3) {
            var dist21e = allStops.get(0).getCoordinates()
                    .tspHeuristic(allStops.get(allStops.size()-1).getCoordinates());
            var dist12e = allStops.get(1).getCoordinates()
                    .tspHeuristic(allStops.get(allStops.size()-1).getCoordinates());

            if (dist21e < dist12e) {
                var temp = allStops.get(0);
                allStops.set(0, allStops.get(1));
                allStops.set(1, temp);
            }
        }

        double dist = 0;
        for (int i = 0; i < allStops.size()-1; i++) {
            var x = allStops.get(i);
            var y = allStops.get(i+1);
            dist += x.getCoordinates().tspHeuristic(y.getCoordinates());
        }
        return dist;
    }

    private ArrayList<Stop> getStops() {
        var unorderedShops =
                myMenu.getDeliveryStops(this.orderItems.toArray(new String[orderItems.size()]));

        ArrayList<Stop> allStops = new ArrayList<>();
        for (Shop shop : unorderedShops) {
            var coords = new What3Words(shop.location).getCoordinates();
            var s = new Stop(shop.name,coords,this.orderNo);
            allStops.add(s);
        }
        allStops.add(new Stop(this.customer,this.destination,this.orderNo));
        return allStops;
    }

    private ArrayList<String> fetchOrderItems(String orderno){
        try {
            Connection conn = DriverManager.getConnection(DatabaseIO.jdbcString);
            final String itemsQuery = "select * from orderDetails where orderNo=(?)";
            PreparedStatement psItemsQuery = conn.prepareStatement(itemsQuery);
            psItemsQuery.setString(1,orderNo);

            ArrayList<String> itemsList = new ArrayList<>();
            ResultSet rs = psItemsQuery.executeQuery();
            while(rs.next()) {
                String item = rs.getString("item");
                itemsList.add(item);
            }
            return itemsList;
        } catch (SQLException throwables) {
            System.err.println("ERROR Reading from database.");
        }
        return null;
    }

    public LongLat getStart() {
        return this.allStops.get(0).getCoordinates();
    }

    public LongLat getDestination() {
        return this.allStops.get(this.allStops.size()-1).getCoordinates();
    }

    public ArrayList<Stop> getAllStops() {
        return this.allStops;
    }

    public double getEstimatedDistance() {
        return this.estimatedDistance;
    }

    public int getDeliveryCost() {
        return this.deliveryCost;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getDestinationW3W() {
        return this.What3Words;
    }








}
