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

    private final Menus myMenu = Menus.getInstance();
    private final Navigation myNavigation = Navigation.getInstance();

    public Order(String orderNo, String customer, String deliverTo) {
        this.customer = customer;
        this.orderNo = orderNo;
        this.What3Words = deliverTo;
        this.destination = new What3Words(deliverTo).getCoordinates();

        this.orderItems = fetchOrderItems();
        this.allStops = getStops();
        this.internalRoute = this.generateInternalRoute();
        this.deliveryCost = myMenu.getDeliveryCost(this.orderItems.toArray(new String[orderItems.size()]));
       // this.printFlight();
    }

    private ArrayList<DroneMove> generateInternalRoute() {
        assert allStops.size() >=2 : "only one stop on entire journey";

        //if there are two pickups, then try arranging so that the last pickup is closest to destination.
        if (allStops.size() == 3) {
            var dist21e = myNavigation.getRoute(this.orderNo, allStops.get(0).getCoordinates(),
                    allStops.get(allStops.size()-1).getCoordinates()).size();
            var dist12e = myNavigation.getRoute(this.orderNo, allStops.get(1).getCoordinates(),
                    allStops.get(allStops.size()-1).getCoordinates()).size();

            if (dist21e < dist12e) {
                var temp = allStops.get(0);
                allStops.set(0, allStops.get(1));
                allStops.set(1, temp);
            }
        }

        ArrayList<DroneMove> route = new ArrayList<>();

        //add pickups to the route.
        route.add(new DroneMove(this.orderNo, allStops.get(0).getCoordinates(),
                allStops.get(0).getCoordinates(),LongLat.JUNK_ANGLE)); //hover at start

        for (int i = 1; i < allStops.size(); i++) {
            var latest = route.get(route.size()-1);
            var stopNext = allStops.get(i);

            route.addAll(myNavigation.getRoute(this.orderNo, latest.getTo(), stopNext.getCoordinates()));

            var last = route.get(route.size()-1);
            route.add(new DroneMove(this.orderNo, last.getTo(), last.getTo(),LongLat.JUNK_ANGLE));
        }
        return route;
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

    private ArrayList<String> fetchOrderItems(){
        try {
            Connection conn = DriverManager.getConnection(DatabaseIO.jdbcString);
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
            System.err.println("ERROR Reading from database.");
            throwables.printStackTrace();
        }
        return null;
    }

    public LongLat getStart() {
        return this.internalRoute.get(0).getFrom();
    }

    public LongLat getDestination() {
        return this.internalRoute.get(internalRoute.size()-1).getTo();
    }

    public ArrayList<Stop> getAllStops() {
        return this.allStops;
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
//        System.out.println("/////// order");
//        DroneMove.getMovesAsFC(internalRoute);
//        for (Stop allStop : this.allStops) {
//            System.out.println(allStop.id);
//        }

        for (int i = 0; i < internalRoute.size()-1; i++) {
            var dm = internalRoute.get(i);
            var dm2 = internalRoute.get(i+1);
           // System.out.println(dm);

            if (!(dm.getTo().equals(dm2.getFrom()))) {
                System.out.println(dm);
               System.out.println("UH OH!");
                System.out.println(dm2);
                }
        }

//        System.out.println(this.internalRoute.get(0));
//        System.out.println(this.internalRoute.get(internalRoute.size()-1));
    }







}
