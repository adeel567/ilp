package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

public class Order {
    private String orderNo;
    private ArrayList<String> orderItems;
    private int deliveryCost;
    private ArrayList<Stop> allStops;
    private ArrayList<DroneMove> internalRoute;
    private LongLat destination;
    private String customer;
    private String What3Words;

    private final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";
    private final Menus myMenu = new Menus("localhost","9898");
    private final Mapping myMapping = new Mapping("localhost", "9898");

    public Order(String orderNo, String customer, String deliverTo) {
        this.customer = customer;
        this.orderNo = orderNo;
        this.What3Words = deliverTo;
        this.destination = new What3Words(deliverTo).getCoordinates();

        this.orderItems = fetchOrderItems();
        this.allStops = getStops();
        this.internalRoute = this.generateInternalRoute();
        this.deliveryCost = myMenu.getDeliveryCost(this.orderItems.toArray(new String[orderItems.size()]));
    }

    private ArrayList<DroneMove> generateInternalRoute() {

        ArrayList<DroneMove> route = new ArrayList<>();

        assert allStops.size() >=2 : "only one stop on entire journey";

        //if there are two pickups, then try arranging so that the last pickup is closest to destination.
        if (allStops.size() == 3) {
            var dist21e = myMapping.getRoute(this.orderNo, allStops.get(0).coordinates,
                    allStops.get(allStops.size()-1).coordinates).size();
            var dist12e = myMapping.getRoute(this.orderNo, allStops.get(1).coordinates,
                    allStops.get(allStops.size()-1).coordinates).size();

            if (dist21e < dist12e) {
                var temp = allStops.get(0);
                allStops.set(0, allStops.get(1));
                allStops.set(1, temp);
            }
        }

        for (int i = 0; i < allStops.size()-1; i++) {
            var stop = allStops.get(i);
            var stopNext = allStops.get(i+1);

            route.add(new DroneMove(this.orderNo, stop.coordinates,stop.coordinates,-999));
            route.addAll(myMapping.getRoute(this.orderNo, stop.coordinates,stopNext.coordinates));
        }
        var dest = allStops.get(allStops.size()-1);
        route.add(new DroneMove(this.orderNo, dest.coordinates,dest.coordinates,-999));

        return route;
    }

    private ArrayList<Stop> getStops() {
        var unorderedShops = myMenu.getDeliveryStops(this.orderItems.toArray(new String[orderItems.size()]));

        ArrayList<Stop> allStops = new ArrayList<>();
        for (Shop shop : unorderedShops) {
            var coords = new What3Words(shop.location).coordinates;
            var s = new Stop(shop.name,coords);
            allStops.add(s);
        }
        allStops.add(new Stop(this.customer,this.destination));
        return allStops;
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
        return this.allStops.get(0).coordinates;
    }

    public LongLat getDestination() {
        return this.destination;
    }

    public int getMovesUsed() {
        return this.internalRoute.size();
    }

    public int getDeliveryCost() {
        return this.deliveryCost;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public ArrayList<DroneMove> getFlightPath() {
        return this.internalRoute;
    }

    public String getDestinationW3W() {
        return this.What3Words;
    }

    public void printFlight() {
        System.out.println("/////// order");
        DroneMove.getMovesAsFC(internalRoute);
        for (Stop allStop : this.allStops) {
            System.out.println(allStop.id);
        }

        for (int i = 0; i < internalRoute.size(); i++) {
            var dm = internalRoute.get(i);
          //  var dm2 = internalRoute.get(i+1);
            System.out.println(dm);

//            if (!(dm.getTo().closeTo(dm2.getFrom()))) {
//                System.out.println(dm.toString());
//                System.out.println("UH OH!");
//                System.out.println(dm2.toString());
//                }
        }
//            System.out.println(droneMove.toString());
    }







}
