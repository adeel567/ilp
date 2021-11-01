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

    private final String jdbcString = "jdbc:derby://localhost:1527/derbyDB";
    private final Menus myMenu = new Menus("localhost","9898");
    private final Mapping myMapping = new Mapping("localhost", "9898");

    public Order(String orderNo, String customer, String deliverTo) {
        this.customer = customer;
        this.orderNo = orderNo;
        this.destination = new What3Words(deliverTo).getCoordinates();
        this.orderItems = fetchOrderItems();
        this.allStops = getStops();
        this.internalRoute = this.generateInternalRoute();
        this.deliveryCost = myMenu.getDeliveryCost(this.orderItems.toArray(new String[orderItems.size()]));
     //   printFlight();
    }

    private ArrayList<DroneMove> generateInternalRoute() {

        ArrayList<DroneMove> route = new ArrayList<>();

        assert allStops.size() >=2 : "only one stop on entire journey";
        for (int i = 0; i < allStops.size()-1; i++) {
            var stop = allStops.get(i);
            var stopNext = allStops.get(i+1);

            route.add(new DroneMove(stop.coordinates,stop.coordinates,-999));
            route.addAll(myMapping.getRoute(stop.coordinates,stopNext.coordinates));
        }
        var dest = allStops.get(allStops.size()-1);
        route.add(new DroneMove(dest.coordinates,dest.coordinates,-999));

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

    public void printFlight() {
        System.out.println("/////// order");
        myMapping.getRouteAsFC(myMapping.movesToPath(internalRoute));
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

   // public ArrayList<Stop> getAllStops() { return this.stops;}







}
