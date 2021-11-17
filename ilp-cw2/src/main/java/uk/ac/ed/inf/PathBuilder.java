package uk.ac.ed.inf;

import org.jgrapht.graph.*;

import java.util.ArrayList;
import java.util.Collections;

public class PathBuilder {

    //constants for number of moves allowed and the location of drone's home.
    private static final int MOVES_ALLOWED = 1500;
    private static final double AT_LONGITUDE = -3.186874;
    private static final double AT_LATITUDE = 55.944494;

    //store all original orders, which orders will be delivered, and the path for delivering such orders.
    private final OrderHandler todaysOrders;
    private ArrayList<String> ordersCompleted;
    private ArrayList<DroneMove> flightPath;

    //graph used for optimising order permutation.
    private SimpleDirectedWeightedGraph<String,tspEdge> originalGraph;

    //where will the drone start and end
    private final Stop start;
    private final Stop end;

    private final NoFlyZones myNoFlyZones = NoFlyZones.getInstance();

    //statistics
    private int profitX;
    private int profitLostX;
    private double monetaryValue;


    /**
     * Class for constructing the optimal* delivery route for the day.
     * @param todaysOrders order handler for today's orders.
     */
    public PathBuilder(OrderHandler todaysOrders) {
        this.todaysOrders = todaysOrders;
        this.start = new Stop("START", new LongLat(AT_LONGITUDE, AT_LATITUDE),"START");
        this.end = new Stop("END", new LongLat(AT_LONGITUDE, AT_LATITUDE),"END");

        System.out.println("ALL " +todaysOrders.getAllOrderNos().size()+ " ORDERS: " + todaysOrders.getAllOrderNos());
    }

    /**
     * Builds an undirected weighted graph of Start and all Orders to be completed.
     * The edges are weights calculated from the cost of the order to be completed and the total
     * distance from flying to the start point from current position until the end.
     */
    public void buildGraph(){
        var initialGraph = new SimpleDirectedWeightedGraph<String, tspEdge>(tspEdge.class);

        for (var orderNo : todaysOrders.getAllOrderNos()) {
            initialGraph.addVertex(orderNo);
        }

        for (var x: initialGraph.vertexSet()) {
            for (var y: initialGraph.vertexSet()) {
                if (!x.equals(y)) {
                    var ordX = todaysOrders.get(x);
                    var ordY = todaysOrders.get(y);

                    var totalDist = ordY.getEstimatedDistance() + ordX.getDestination().tspHeuristic(ordY.getStart());
                    var weight = totalDist/(double) ordY.getDeliveryCost();

                    addEdge(initialGraph, x, y, weight);
                }
            }
        }

        initialGraph.addVertex(start.getId()); //two separate loops because start is not an order.
        for (var y : initialGraph.vertexSet()) {
            if (!y.equals(start.getId())) {
                var ordY = todaysOrders.get(y);

                var totalDist = ordY.getEstimatedDistance() + start.getCoordinates().tspHeuristic(ordY.getStart());
                var weight = totalDist / (double) ordY.getDeliveryCost();

                addEdge(initialGraph, start.getId(), y, weight);
            }
        }
        this.originalGraph = initialGraph;
    }

    /**
     * Add edge to graph
     * @param g directed weighted graph
     * @param x from node
     * @param y to node
     * @param weight weight between the two nodes
     */
    private void addEdge(SimpleDirectedWeightedGraph<String, tspEdge> g, String x, String y, double weight) {
        var edge = new tspEdge(weight);
        g.addEdge(x, y, edge);
        g.setEdgeWeight(edge, weight);

    }

    /**
     * Add edges from all orders to end location.
     * This is used when moves are over what is allowed, in order to find which node to drop.
     * @param g directed weighted graph without End.
     */
    private void addEnd(SimpleDirectedWeightedGraph<String, tspEdge> g) {
        g.addVertex(end.getId());
        for (var x : g.vertexSet()) {
            if (!(x.equals(end.getId()) || x.equals(start.getId())) ) {
                var ordX = todaysOrders.get(x);

                var totalDist = ordX.getEstimatedDistance() + ordX.getDestination().tspHeuristic(end.getCoordinates());
                var weight = totalDist/ (double) ordX.getDeliveryCost();

                addEdge(g, x, end.getId(), weight);
            }
        }
    }

    /**
     * Check if there are still orders to visit on graph.
     * @param g directed weighted graph.
     * @param vert order we are currently at.
     * @return true if there are more edges, thus more orders to complete.
     */
    private Boolean hasNextEdge (SimpleDirectedWeightedGraph<String,tspEdge> g, String vert) {
        return g.outDegreeOf(vert) > 0;
    }

    /**
     * Figure out which order to complete next.
     * This is done in a greedy fashion, by selecting a visitable order with the lowest weight.
     * @param g directed weighted graph with orders still to visit.
     * @param from order we are currently at.
     * @return edge that we will travel along
     */
    private tspEdge greedyNextEdge(SimpleDirectedWeightedGraph<String,tspEdge> g, String from) {
        return (Collections.min(g.outgoingEdgesOf(from)));
    }

    /**
     * When moves are over what is allowed, we find the node with the 'worst' weight
     * to the end location in order to remove it.
     * @param g directed weighted graph with all orders and end node.
     * @return orderNo of 'worst' order.
     */
    private String worstEndVert(SimpleDirectedWeightedGraph<String,tspEdge> g) {
        var x = g.getEdgeSource(Collections.max(g.incomingEdgesOf(end.getId())));
        System.out.println("REMOVED: " + x);
        return x;
    }

    /**
     * Construct a tour of the orders by going from the start and greedily picking the best
     * next order. Once all orders have been added, construct a drone to fly over. if the
     * moves of the drone are within the allowed limit we terminate, otherwise remove the
     * worst node and try again.
     */
    public void doTour() {
        //copy of graph is needed to delete and add vertexes.
        //as no underlying modification is being made, a shallow copy is all that is needed.
        var preserveGraph  = (SimpleDirectedWeightedGraph<String,tspEdge>) originalGraph.clone();
        var movesUsed = 0;
        ArrayList<String> perms = null;
        ArrayList<DroneMove> flight = null;
        var curr = start.getId();
        ArrayList<String> removed = new ArrayList<>();

        while (movesUsed > MOVES_ALLOWED || curr.equals(start.getId())) {
            var whileGraph  = (SimpleDirectedWeightedGraph<String,tspEdge>) preserveGraph.clone();
            curr = start.getId();
            perms = new ArrayList<String>();

            while (hasNextEdge(whileGraph, curr)) { //keep doing if there are still edges to be visited
                var nextEdge = greedyNextEdge(whileGraph, curr); //get next edge
                var next = whileGraph.getEdgeTarget(nextEdge); //get next order
                whileGraph.removeVertex(curr); //pop order as it's been visited
                curr = next;
                perms.add(next);
            }

            var allStopsMade = allStopsMade(perms);
            var currentDrone = flightFromStopsMade(allStopsMade);
            flight = currentDrone.getFlightPath();
            movesUsed = currentDrone.movesUsed();

            if (movesUsed > MOVES_ALLOWED) { //prepare for next loop
                //get order that is 'worst' from end location, remove from the graph that will
                //be reset for use in the next loop

                System.out.println("CURRENT PERM: "+ perms);
                System.out.println("CURRENT MOVES USED " + movesUsed);

                var fixEnds  = (SimpleDirectedWeightedGraph<String,tspEdge>) preserveGraph.clone(); //reset
                addEnd(fixEnds); //add edges to end location
                removed.add(worstEndVert(fixEnds)); //get 'worst' and add to removed

                preserveGraph.removeAllVertices(removed); //setup for next iteration
            }
        }


        System.out.println("FINAL " +perms.size()+ " PERMS " + perms);
        this.ordersCompleted = new ArrayList<>();
        this.ordersCompleted.addAll(perms);
        this.ordersCompleted.remove(start.getId());
        this.ordersCompleted.remove(end.getId());

        this.flightPath = flight;

        this.profitX = calcProfit(perms);
        this.profitLostX = calcProfitLost(removed);
        this.monetaryValue = calcMonetaryValue();
        System.out.println("MOVES TAKEN: " + flight.size());
    }

    /**
     * Calculate the amount of money made from the orders delivered.
     * @param perms orderNos being delivered.
     * @return amount of money.
     */
    private int calcProfit(ArrayList<String> perms) {
        int profit = 0;
        for (String perm : perms) {
            if (!(perm.equals("START") || perm.equals("END"))) {
                profit+= todaysOrders.get(perm).getDeliveryCost();

            }
        }
        System.out.println("TOTAL PROFIT $$$ : " + profit);
        return profit;
    }

    /**
     * Calculate how much money has been lost from undeliverable orders.
     * @param removed which orderNos will not be delivered.
     * @return amount of money lost.
     */
    private int calcProfitLost(ArrayList<String> removed) {
        int lost = 0;
        for (String s : removed) {
            lost+=todaysOrders.get(s).getDeliveryCost();
        }
        System.out.println("TOTAL COST LOST: " + lost);
        return lost;
    }

    private double calcMonetaryValue() {
        var m =  (profitX/(double) (profitX+profitLostX));
        System.out.printf("MONETARY VALUE: %.2f%n",m);
        return m;
    }

    /**
     * Build a drone and its route from the stops to be made on a day's orders.
     * @param allStops stops to be made.
     * @return a Drone object with a completed flightpath.
     */
    public Drone flightFromStopsMade(ArrayList<Stop> allStops) {
        var drone = new Drone(allStops.get(0).getCoordinates());

        for (int i = 1, allStopsSize = allStops.size()-1; i < allStopsSize; i++) {
            Stop stop = allStops.get(i);

            drone.flyToStop(stop);
            drone.doHover();
        }
        drone.flyToStop(allStops.get(allStops.size()-1));
        return drone;
    }

    /**
     * Get all the stops to be made on a day's orders
     * @param perms orderNos of deliveries to be made.
     * @return all stops to be made to complete orders.
     */
    private ArrayList<Stop> allStopsMade(ArrayList<String> perms)
    {
        var test = new ArrayList<Stop>();

        test.add(start); //calculate all stops made
        for (var job : perms) {
            test.addAll(this.todaysOrders.get(job).getAllStops());
        }
        test.add(end);

//        for (Stop stop : test) {
//            System.out.println(stop);
//        }
        return test;
    }

    /**
     * Get which Orders will be delivered today.
     * @return all the Order objects from orderNos that will be delivered.
     */
    public ArrayList<Order> getOrdersDelivered() {
        var orders = new ArrayList<Order>();
        for (String s : ordersCompleted) {
            orders.add(todaysOrders.get(s));
        }
        return orders;
    }

    public ArrayList<DroneMove> getFlightPath() {
        return this.flightPath;
    }

}

