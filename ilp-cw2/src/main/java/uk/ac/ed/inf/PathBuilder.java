package uk.ac.ed.inf;

import org.jgrapht.graph.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PathBuilder {


    private static final int MOVES_ALLOWED = 1500;
    private static final double AT_LONGITUDE = -3.186874;
    private static final double AT_LATITUDE = 55.944494;

    private final HashMap<String,Order> todaysOrders;
    private ArrayList<String> ordersCompleted;
    private ArrayList<DroneMove> flightPath;

    private SimpleDirectedWeightedGraph<String,tspEdge> realGraph;

    private final Stop start;
    private final Stop end;

    private final Navigation myNavigation = Navigation.getInstance();

    //statistics
    private int profitX;
    private int profitLostX;



    public PathBuilder(HashMap<String,Order> todaysOrders) {
        this.todaysOrders = todaysOrders;
        this.start = new Stop("START", new LongLat(AT_LONGITUDE, AT_LATITUDE),"START");
        this.end = new Stop("END", new LongLat(AT_LONGITUDE, AT_LATITUDE),"END");

        System.out.println("ALL " +todaysOrders.keySet().size()+ " ORDERS: " + todaysOrders.keySet());
    }

    public void buildGraph(){

        var initialGraph = new SimpleDirectedWeightedGraph<String, tspEdge>(tspEdge.class);

        for (var orderNo : todaysOrders.keySet()) {
            initialGraph.addVertex(orderNo);
        }

        for (var x: initialGraph.vertexSet()) {
            for (var y: initialGraph.vertexSet()) {
                if (!x.equals(y)) {
                    var ordX = todaysOrders.get(x);
                    var ordY = todaysOrders.get(y);

                    var totalDist = ordY.getTotalDistance() + ordX.getDestination().tspHeuristic(ordY.getStart());
                    var weight = totalDist/(double) ordY.getDeliveryCost();

                    var edge = new tspEdge(weight, null);
                    initialGraph.addEdge(x,y,edge);
                    initialGraph.setEdgeWeight(edge,weight);
                }
            }
        }

        initialGraph.addVertex(start.getId()); //two separate loops because start is not an order.
        for (var y : initialGraph.vertexSet()) {
            if (!y.equals(start.getId())) {
                var ordY = todaysOrders.get(y);

                var totalDist = ordY.getTotalDistance() + start.getCoordinates().tspHeuristic(ordY.getStart());
                var weight = totalDist / (double) ordY.getDeliveryCost();

                var edge = new tspEdge(weight, null);
                initialGraph.addEdge(start.getId(),y,edge);
                initialGraph.setEdgeWeight(edge,weight);
            }
        }

        this.realGraph = initialGraph;
    }

    private void addEnd(SimpleDirectedWeightedGraph<String, tspEdge> g) {
        g.addVertex(end.getId());
        for (var x : g.vertexSet()) {
            if (!(x.equals(end.getId()) || x.equals(start.getId())) ) {
                var ordX = todaysOrders.get(x);

                var totalDist = ordX.getDestination().tspHeuristic(end.getCoordinates());
                var weight = totalDist / (double) ordX.getDeliveryCost();

                var edge = new tspEdge(weight, null);
                g.addEdge(x,end.getId(),edge);
                g.setEdgeWeight(edge,weight);
            }
        }
    }

    private Boolean hasNextEdge (SimpleDirectedWeightedGraph<String,tspEdge> g, String vert) {
        return g.outDegreeOf(vert) > 0;
    }

    private tspEdge greedyNextEdge(SimpleDirectedWeightedGraph<String,tspEdge> g, String from) {
        return (Collections.min(g.outgoingEdgesOf(from)));
    }

    private String worstEndVert(SimpleDirectedWeightedGraph<String,tspEdge> g) {
        var x = g.getEdgeSource(Collections.max(g.incomingEdgesOf(end.getId())));
        System.out.println("REMOVED: " + x);
        return x;
    }

    public void doTour() {
        //copy of graph is needed to delete and add vertexes.
        //as no underlying modification is being made, a shallow copy is all that is needed.
        var preserveGraph  = (SimpleDirectedWeightedGraph<String,tspEdge>) realGraph.clone();
        var movesUsed = 0;
        ArrayList<String> perms = null;
        ArrayList<DroneMove> flight = null;
        var curr = start.getId();
        ArrayList<String> removed = new ArrayList<>();

        while (movesUsed > MOVES_ALLOWED || curr.equals(start.getId())) {
            var whileGraph  = (SimpleDirectedWeightedGraph<String,tspEdge>) preserveGraph.clone();
            movesUsed = 0;
            curr = start.getId();
            perms = new ArrayList<String>();
            //perms.add(start.id);
            //flight = new ArrayList<DroneMove>();

            while (hasNextEdge(whileGraph, curr)) { //keep doing if there are still edges to be visited
                var nextEdge = greedyNextEdge(whileGraph, curr);
              //  movesUsed += nextEdge.getMoves();
              //  flight.addAll(nextEdge.getRoute());
                var next = whileGraph.getEdgeTarget(nextEdge);
                whileGraph.removeVertex(curr); //pop order as it's been visited
                curr = next;
                perms.add(next);
            }

            addEnd(whileGraph); //final order to end location

            var homeEdge = (whileGraph.getEdge(curr,end.getId()));
          //  flight.addAll(homeEdge.getRoute());
          //  movesUsed += homeEdge.getMoves();
          //  perms.add(end.id);

            var allStopsMade = allStopsMade(perms);
            var currentFlightPath = flightFromStopsMade(allStopsMade);
            flight = currentFlightPath;
            movesUsed = currentFlightPath.size();

            if (movesUsed > MOVES_ALLOWED) { //prepare for next loop
                //get order that is 'worst' from end location, remove from the graph that will
                //be reset for use in the next loop

                System.out.println("CURRENT PERM: "+ perms);
                System.out.println("CURRENT MOVES USED " + movesUsed);

                var fixEnds  = (SimpleDirectedWeightedGraph<String,tspEdge>) preserveGraph.clone();
                addEnd(fixEnds);
                removed.add(worstEndVert(fixEnds));

                preserveGraph.removeAllVertices(removed);
            }
        }


        System.out.println("FINAL " +perms.size()+ " PERMS " + perms);
        this.ordersCompleted = new ArrayList<>();
        this.ordersCompleted.addAll(perms);
        this.ordersCompleted.remove(start.getId());
        this.ordersCompleted.remove(end.getId());

        this.flightPath = flight;

        ///////statistics///////
        this.profitX = calcProfit(perms);
        this.profitLostX = calcProfitLost(removed);
        System.out.println("MONTEREY THING: " + (profitX/(double) (profitX+profitLostX)));
        System.out.println("MOVES TAKEN: " + flight.size());

        for (int i = 0; i < flight.size() - 1; i++) {
            var dm = flight.get(i);
            var dm2 = flight.get(i + 1);
            if (!(dm.getTo().equals(dm2.getFrom()))) {
                System.err.println(dm);
                System.err.println("ILLEGAL MOVE!");
                System.err.println(dm2);
            }
        }

    }

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

    private int calcProfitLost(ArrayList<String> removed) {
        int lost = 0;
        for (String s : removed) {
            lost+=todaysOrders.get(s).getDeliveryCost();
        }
        System.out.println("TOTAL COST LOST: " + lost);
        return lost;
    }


    public ArrayList<DroneMove> flightFromStopsMade(ArrayList<Stop> test) {
        ArrayList<DroneMove> route = new ArrayList<>();

        //add first journey on its own
        var a = test.get(0);
        var b = test.get(1);
        route.addAll(myNavigation.getRoute(b.getOrderNo(),a.getCoordinates(),b.getCoordinates()));
        var latestC = route.get(route.size()-1).getTo();
        route.add(new DroneMove(b.getOrderNo(),latestC,latestC,LongLat.JUNK_ANGLE)); //hover after first stop

        for (int i = 1; i < test.size()-1; i++) {
             var x = route.get(route.size()-1).getTo();
             var y = test.get(i+1);
            route.addAll(myNavigation.getRoute(y.getOrderNo(),x,y.getCoordinates()));
             latestC = route.get(route.size()-1).getTo();
            route.add(new DroneMove(y.getOrderNo(),latestC,latestC,LongLat.JUNK_ANGLE));
        }
        return route;
    }

    private ArrayList<Stop> allStopsMade(ArrayList<String> perms)
    {
        var test = new ArrayList<Stop>();

        test.add(start); //calculate all stops made
        for (var job : perms) {
            test.addAll(this.todaysOrders.get(job).getAllStops());
        }
        test.add(end);

        for (Stop stop : test) {
            System.out.println(stop);
        }
        return test;
    }


    public ArrayList<DroneMove> getFlightPath() {
        return this.flightPath;
    }

    public ArrayList<Order> getOrdersDelivered() {
        var orders = new ArrayList<Order>();
        for (String s : ordersCompleted) {
            orders.add(todaysOrders.get(s));
        }
        return orders;
    }

}

