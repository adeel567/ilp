package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class representing a drone delivery order.
 */
public class Order {
    /** Instance of Menus */
    private final Menus myMenu = Menus.getInstance();

    /** Order number for this order */
    private final String orderNo;

    /** ArrayList of all the items in this order */
    private final ArrayList<String> orderItems;

    /** Calculated total value of this delivery */
    private final int deliveryCost;

    /** All pickups/drop-offs for this order */
    private final ArrayList<Stop> allStops;

    /** Drop-off location for this order */
    private final LongLat destination;

    /** Identifier for customer of order */
    private final String customer;

    /** Drop-off location as What3Words */
    private final String What3Words;

    /** Estimated internal distance for an order to be completed */
    private final double estimatedDistance;


    /**
     * Create an Order object using information from Orders database table.
     *
     * @param orderNo   orderNo of order obtained.
     * @param customer  customer who order belongs to.
     * @param deliverTo W3W of delivery location.
     */
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

    /**
     * Communicates with database to retrieve information for the order using the order number.
     *
     * @param orderNo of order to retrieve.
     * @return a collection of the items in the order.
     */
    private ArrayList<String> fetchOrderItems(String orderNo) {
        try {
            Connection conn = DriverManager.getConnection(DatabaseIO.getDBString());
            final String itemsQuery = "select * from orderDetails where orderNo=(?)";
            PreparedStatement psItemsQuery = conn.prepareStatement(itemsQuery);
            psItemsQuery.setString(1, orderNo);

            ArrayList<String> itemsList = new ArrayList<>();
            ResultSet rs = psItemsQuery.executeQuery();
            while (rs.next()) {
                String item = rs.getString("item");
                itemsList.add(item);
            }
            return itemsList;
        } catch (SQLException throwables) {
            System.err.println("ERROR Reading from database.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Obtains all the stops that need to be visited for this order, including the destination.
     *
     * @return a collection of all the stops to be visited.
     */
    private ArrayList<Stop> getStops() {
        //use Menus class to gather all the shops
        var unorderedShops =
                myMenu.getDeliveryShops(this.orderItems.toArray(new String[orderItems.size()]));

        ArrayList<Stop> allStops = new ArrayList<>();
        for (Shop shop : unorderedShops) {
            var coords = new What3Words(shop.location).getCoordinates();
            var s = new Stop(shop.name, coords, this.orderNo);
            allStops.add(s);
        }
        allStops.add(new Stop(this.customer, this.destination, this.orderNo));
        return allStops;
    }


    /**
     * Estimates the distance covered by visiting all the pickups and destination.
     *
     * @return double of the distance that is estimated to be covered.
     */
    private Double calcEstimatedDistance() {
        assert allStops.size() >= 2 : "only one stop on entire journey";

        //if there are two pickups, then try arranging so that the last pickup is closest to destination.
        if (allStops.size() == 3) {
            var dist21e = allStops.get(0).getCoordinates()
                    .tspHeuristic(allStops.get(allStops.size() - 1).getCoordinates());
            var dist12e = allStops.get(1).getCoordinates()
                    .tspHeuristic(allStops.get(allStops.size() - 1).getCoordinates());

            if (dist21e < dist12e) {
                var temp = allStops.get(0);
                allStops.set(0, allStops.get(1));
                allStops.set(1, temp);
            }
        }

        double dist = 0;
        for (int i = 0; i < allStops.size() - 1; i++) {
            var x = allStops.get(i);
            var y = allStops.get(i + 1);

            var locDist = x.getCoordinates().tspHeuristic(y.getCoordinates());
            dist += locDist;
        }
        return dist;
    }

    public LongLat getStartCoords() {
        return this.allStops.get(0).getCoordinates();
    }

    public LongLat getDestinationCoords() {
        return this.allStops.get(this.allStops.size() - 1).getCoordinates();
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
