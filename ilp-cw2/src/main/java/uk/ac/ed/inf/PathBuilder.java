package uk.ac.ed.inf;

import org.jgrapht.Graphs;
import org.jgrapht.graph.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Takes a day's orders and constructs the optimal path for the drone to take.
 */
public class PathBuilder implements PathBuilderInterface {

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
    private int profit;
    private int profitLost;
    private double monetaryValue;


    /**
     * Construct by providing today's orders of which to build best route for.
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
    @Override
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

                    var totalDist = ordY.getEstimatedDistance() +
                            ordX.getDestinationCoords().tspHeuristic(ordY.getStartCoords());
                    var cost = (double) ordY.getDeliveryCost();

                    addEdge(initialGraph, x, y, totalDist, cost);
                }
            }
        }

        initialGraph.addVertex(start.getId()); //two separate loops because start is not an order.
        for (var y : initialGraph.vertexSet()) {
            if (!y.equals(start.getId())) {
                var ordY = todaysOrders.get(y);

                var totalDist = ordY.getEstimatedDistance() +
                        start.getCoordinates().tspHeuristic(ordY.getStartCoords());
                var cost =  (double) ordY.getDeliveryCost();

                addEdge(initialGraph, start.getId(), y, totalDist, cost);
            }
        }
        this.originalGraph = initialGraph;
    }

    /**
     * Add edge to graph.
     * @param g directed weighted graph
     * @param x from node
     * @param y to node
     * @param mini term to minimise in weight calculation.
     * @param maxi term to maximise in weight calculation.
     */
    private void addEdge(SimpleDirectedWeightedGraph<String, tspEdge> g, String x, String y, double mini, double maxi) {
        var weight = mini/maxi;

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

                var totalDist = ordX.getEstimatedDistance()
                        + ordX.getDestinationCoords().tspHeuristic(end.getCoordinates());
                var cost =  (double) ordX.getDeliveryCost();

                addEdge(g, x, end.getId(), totalDist, cost);
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
        g.removeVertex(x);
        System.out.println("REMOVED: " + x);
        return x;
    }

    @Override
    public void doTour() {
        var movesUsed = 0;
        var curr = start.getId();
        ArrayList<String> greedyPerm = getGreedyPerm(curr);
        ArrayList<String> visitedOrders = new ArrayList<>();
        System.out.println("GREEDY PERMS: " +greedyPerm);

        Drone drone = new Drone(start.getCoordinates());
        for (String s : greedyPerm) {
            var ord = todaysOrders.get(s);
            for (Stop stop : ord.getAllStops()) {
                drone.flyToStop(stop);
                drone.doHover();
            }
            visitedOrders.add(s);

            movesUsed = drone.getMovesUsed();

            if (movesUsed >= MOVES_ALLOWED) {
                break;
            }
        }

        boolean canFlyHome = false;

        if (MOVES_ALLOWED >= (drone.movesTo(end.getCoordinates())+movesUsed)) {
            canFlyHome = true;
        }

        while (movesUsed > MOVES_ALLOWED || !canFlyHome) {
            drone.rollbackOrder();
            movesUsed = drone.getMovesUsed();
            visitedOrders.remove(visitedOrders.size()-1);

            canFlyHome = MOVES_ALLOWED >= (drone.movesTo(end.getCoordinates())+movesUsed);
        }

        drone.flyToStop(end);

        System.out.println("FINAL " +visitedOrders.size()+ " PERMS " + visitedOrders);
        this.ordersCompleted = new ArrayList<>();
        this.ordersCompleted.addAll(visitedOrders);

        this.flightPath = drone.getFlightPath();

        this.profit = calcProfit(ordersCompleted);

        greedyPerm.removeAll(ordersCompleted);

        this.profitLost = calcProfitLost(greedyPerm);
        this.monetaryValue = calcMonetaryValue();
        System.out.println("MOVES TAKEN: " + this.flightPath.size());
    }

    private ArrayList<String> getGreedyPerm(String curr) {
        ArrayList<String> ordering = new ArrayList<>();

        var gg  = shallowCopyOf(originalGraph);

        while (hasNextEdge(gg, curr)) {
            var nextEdge = greedyNextEdge(gg, curr); //get next edge
            var next = gg.getEdgeTarget(nextEdge); //get next order
            gg.removeVertex(curr); //pop order as it's been visited
            curr = next;
            ordering.add(next);
        }
        return ordering;
    }


    /**
     * Create a shallow copy of a graph by adding all of its vertices and edges to a new graph.
     * This is so vertices can be 'popped' and the graph reset after without a full rebuild.
     * @param g graph to copy from.
     * @return a shallow copy of the given graph.
     */
    private SimpleDirectedWeightedGraph<String,tspEdge> shallowCopyOf(SimpleDirectedWeightedGraph<String,tspEdge> g) {
        var newG = new SimpleDirectedWeightedGraph<String,tspEdge>(tspEdge.class);
        Graphs.addGraph(newG,g);
        return newG;
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
        var m =  (profit /(double) (profit + profitLost));
        System.out.printf("MONETARY VALUE: %.2f%n",m);
        return m;
    }

    /**
     * Build a drone and its route from the stops to be made on a day's orders.
     * Will terminate early if moves are over what is allowed, so it may not visit all stops.
     * @param allStops stops to be made.
     * @return a Drone object with a completed flightpath.
     */
    private Drone flightFromStopsMade(ArrayList<Stop> allStops) {
        var drone = new Drone(allStops.get(0).getCoordinates());

        for (int i = 1, allStopsSize = allStops.size()-1; i < allStopsSize; i++) {
            Stop stop = allStops.get(i);

            drone.flyToStop(stop);
            drone.doHover();
        }
        drone.flyToStop(allStops.get(allStops.size()-1)); //fly home
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
        return test;
    }

    /**
     * Get which Orders will be delivered today.
     * @return all the Order objects from orderNos that will be delivered.
     */
    @Override
    public ArrayList<Order> getOrdersDelivered() {
        var orders = new ArrayList<Order>();
        for (String s : ordersCompleted) {
            orders.add(todaysOrders.get(s));
        }
        return orders;
    }

    @Override
    public ArrayList<DroneMove> getFlightPath() {
        return this.flightPath;
    }

    @Override
    public int getProfit() {
        return profit;
    }

    @Override
    public int getProfitLost() {
        return profitLost;
    }

    @Override
    public double getMonetaryValue() {
        return monetaryValue;
    }

}

