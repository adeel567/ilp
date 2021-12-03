package uk.ac.ed.inf;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Takes a day's orders and constructs the optimal path for the drone to take.
 */
public class PathBuilder implements PathBuilderInterface {

    /** Number of moves the drone is allowed to take */
    private static final int MOVES_ALLOWED = 1500;

    /**Appleton tower longitude*/
    private static final double AT_LONGITUDE = -3.186874;

    /**Appleton tower latitude*/
    private static final double AT_LATITUDE = 55.944494;

    /**OrderHandler of all orders to be completed*/
    private final OrderHandler todaysOrders;

    /** Drone start location as a Stop */
    private final Stop start;

    /** Drone end location as a Stop */
    private final Stop end;

    /** Instance of No-Fly Zones class */
    private final NoFlyZones myNoFlyZones = NoFlyZones.getInstance();

    /** ArrayList of order numbers for orders which were successfully delivered */
    private ArrayList<String> ordersCompleted;

    /** The final flightpath taken by the drone as DroneMoves */
    private ArrayList<DroneMove> flightPath;

    /** Graph of orders with edges to each other, for finding greedy permutation */
    private SimpleDirectedWeightedGraph<String, tspEdge> originalGraph;

    /** Total value of deliveries made */
    private int profit;

    /** Total value of deliveries which were not made */
    private int profitLost;

    /** Monetary value statistic */
    private double monetaryValue;


    /**
     * Construct by providing today's orders of which to build best route for.
     *
     * @param todaysOrders order handler for today's orders.
     */
    public PathBuilder(OrderHandler todaysOrders) {
        this.todaysOrders = todaysOrders;
        this.start = new Stop("START", new LongLat(AT_LONGITUDE, AT_LATITUDE), "START");
        this.end = new Stop("END", new LongLat(AT_LONGITUDE, AT_LATITUDE), "END");

        System.out.println("ALL " + todaysOrders.getAllOrderNos().size() + " ORDERS: " + todaysOrders.getAllOrderNos());
    }

    /**
     * Builds an undirected weighted graph of Start and all Orders to be completed.
     * The edges are weights calculated from the cost of the order to be completed and the total
     * distance from flying to the start point from current position until the end.
     */
    @Override
    public void buildGraph() {
        var initialGraph = new SimpleDirectedWeightedGraph<String, tspEdge>(tspEdge.class);

        for (var orderNo : todaysOrders.getAllOrderNos()) {
            initialGraph.addVertex(orderNo);
        }

        for (var x : initialGraph.vertexSet()) {
            for (var y : initialGraph.vertexSet()) {
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
                var cost = (double) ordY.getDeliveryCost();

                addEdge(initialGraph, start.getId(), y, totalDist, cost);
            }
        }
        this.originalGraph = initialGraph;
    }

    /**
     * Add edge to graph.
     *
     * @param g    directed weighted graph
     * @param x    from node
     * @param y    to node
     * @param mini term to minimise in weight calculation.
     * @param maxi term to maximise in weight calculation.
     */
    private void addEdge(SimpleDirectedWeightedGraph<String, tspEdge> g, String x, String y, double mini, double maxi) {
        var weight = mini / maxi;

        var edge = new tspEdge(weight);
        g.addEdge(x, y, edge);
        g.setEdgeWeight(edge, weight);

    }

    /**
     * Check if there are still orders to visit on graph.
     *
     * @param g    directed weighted graph.
     * @param vert order we are currently at.
     * @return true if there are more edges, thus more orders to complete.
     */
    private Boolean hasNextEdge(SimpleDirectedWeightedGraph<String, tspEdge> g, String vert) {
        return g.outDegreeOf(vert) > 0;
    }

    /**
     * Figure out which order to complete next.
     * This is done in a greedy fashion, by selecting a visitable order with the lowest weight.
     *
     * @param g    directed weighted graph with orders still to visit.
     * @param from order we are currently at.
     * @return edge that we will travel along
     */
    private tspEdge greedyNextEdge(SimpleDirectedWeightedGraph<String, tspEdge> g, String from) {
        return (Collections.min(g.outgoingEdgesOf(from)));
    }

    /**
     * Uses generated graph and the day's orders to fly drone.
     * Greedy approach is taken on the graph, attempting to complete as many orders as possible and
     * return home within the allowed moves.
     */
    @Override
    public void doTour() {
        var movesUsed = 0;
        ArrayList<String> greedyPerm = getGreedyPerm(start.getId(), originalGraph); //get permutation of orders.
        ArrayList<String> visitedOrders = new ArrayList<>();
        System.out.println("GREEDY PERMS: " + greedyPerm);

        Drone drone = new Drone(start.getCoordinates());
        for (String orderNo : greedyPerm) {
            var ord = todaysOrders.get(orderNo);
            flyThroughOrder(drone, ord);
            visitedOrders.add(orderNo);
            movesUsed = drone.getMovesUsed();

            if (movesUsed >= MOVES_ALLOWED) {
                break;
            }
        }

        boolean canFlyHome = MOVES_ALLOWED >= (drone.movesTo(end.getCoordinates()) + movesUsed);

        while (movesUsed > MOVES_ALLOWED || !canFlyHome) {
            drone.rollbackOrder(); //undo latest order
            visitedOrders.remove(visitedOrders.size() - 1);
            movesUsed = drone.getMovesUsed();

            canFlyHome = MOVES_ALLOWED >= (drone.movesTo(end.getCoordinates()) + movesUsed);
        }

        drone.flyToStop(end);

        //diagnostic information and setting of values
        System.out.println("FINAL " + visitedOrders.size() + " PERMS " + visitedOrders);
        this.ordersCompleted = visitedOrders;
        this.flightPath = drone.getFlightPath();
        this.profit = calcProfit(ordersCompleted);
        greedyPerm.removeAll(ordersCompleted);
        this.profitLost = calcProfitLost(greedyPerm);
        this.monetaryValue = calcMonetaryValue();
        System.out.println("MOVES TAKEN: " + this.flightPath.size());
    }

    /**
     * Takes an order and flies the drone from its current location over the order until the order's destination.
     * Includes the hover moves for the order.
     * @param drone that will be flown.
     * @param order to complete flight of.
     */
    private void flyThroughOrder(Drone drone, Order order) {
        for (Stop stop : order.getAllStops()) {
            drone.flyToStop(stop);
            drone.doHover();
        }
    }

    /**
     * Uses graph to get a permutation of orders by taking the least cost edge from a start position.
     * @param start the node to start the permutation from.
     * @param g graph to run Greedy on.
     * @return ArrayList of orders, arranged 'optimally'.
     */
    private ArrayList<String> getGreedyPerm(String start, SimpleDirectedWeightedGraph<String,tspEdge> g) {
        ArrayList<String> ordering = new ArrayList<>();

        var localGraph = shallowCopyOf(g);

        while (hasNextEdge(localGraph, start)) {
            var nextEdge = greedyNextEdge(localGraph, start); //get next edge
            var next = localGraph.getEdgeTarget(nextEdge); //get next order
            localGraph.removeVertex(start); //pop order as it's been visited
            start = next;
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
    private SimpleDirectedWeightedGraph<String, tspEdge> shallowCopyOf(SimpleDirectedWeightedGraph<String, tspEdge> g) {
        var newG = new SimpleDirectedWeightedGraph<String, tspEdge>(tspEdge.class);
        Graphs.addGraph(newG, g);
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
                profit += todaysOrders.get(perm).getDeliveryCost();

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
            lost += todaysOrders.get(s).getDeliveryCost();
        }
        System.out.println("TOTAL COST LOST: " + lost);
        return lost;
    }

    /**
     * Calculates the monetary value of the orders completed.
     * @return double from 0-1 of monetary value.
     */
    private double calcMonetaryValue() {
        var m = (profit / (double) (profit + profitLost));
        System.out.printf("MONETARY VALUE: %.2f%n", m);
        return m;
    }

    /**
     * Get which Orders will be delivered today.
     * @return all the Order objects from orderNos that have been delivered by drone.
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

