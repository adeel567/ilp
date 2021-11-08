package uk.ac.ed.inf;

import org.jgrapht.graph.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PathBuilder {


    private static final int MOVES_ALLOWED = 1500;
    private static final double AT_LONGITUDE = -3.186874;
    private static final double AT_LATITUDE = 55.944494;

    private HashMap<String,Order> todaysOrders;
    private ArrayList<String> ordersCompleted;
    private ArrayList<DroneMove> flightPath;

    private SimpleDirectedWeightedGraph<String,tspEdge> realGraph;

    private Stop start;
    private Stop end;

    private final Mapping myMapping = new Mapping("localhost", "9898");



    public PathBuilder(HashMap<String,Order> todaysOrders) {
        this.todaysOrders = todaysOrders;
    }

    public void buildGraph(){
        this.start = new Stop("START", new LongLat(AT_LONGITUDE, AT_LATITUDE));
        this.end = new Stop("END", new LongLat(AT_LONGITUDE, AT_LATITUDE));

        var initialGraph = new SimpleDirectedWeightedGraph<String, tspEdge>(tspEdge.class);

        for (var orderNo : todaysOrders.keySet()) {
            initialGraph.addVertex(orderNo);
        }

        for (var x: initialGraph.vertexSet()) {
            for (var y: initialGraph.vertexSet()) {
                if (!x.equals(y)) {
                    var ordX = todaysOrders.get(x);
                    var ordY = todaysOrders.get(y);
                    var route = myMapping.getRoute(ordY.getOrderNo(),
                            ordX.getDestination(), ordY.getStart());
                    route.addAll(ordY.getFlightPath());
                    var totalDist = route.size();
                    var weight = totalDist/(double) ordY.getDeliveryCost();

                    var edge = new tspEdge(weight, route, totalDist);
                    initialGraph.addEdge(x,y,edge);
                    initialGraph.setEdgeWeight(edge,weight);
                }
            }
        }

        initialGraph.addVertex(start.id); //two separate loops because start is not an order.
        for (var y : initialGraph.vertexSet()) {
            if (!y.equals(start.id)) {
                var ordY = todaysOrders.get(y);
                var route = myMapping.getRoute(ordY.getOrderNo(), start.coordinates,
                        ordY.getStart());
                route.addAll(ordY.getFlightPath());
                var totalDist = route.size();
                var weight = totalDist / (double) ordY.getDeliveryCost();

                var edge = new tspEdge(weight, route, totalDist);
                initialGraph.addEdge(start.id,y,edge);
                initialGraph.setEdgeWeight(edge,weight);
            }
        }

        this.realGraph = initialGraph;
    }

    private void addEnd(SimpleDirectedWeightedGraph<String, tspEdge> g) {
        g.addVertex(end.id);
        for (var x : g.vertexSet()) {
            if (!(x.equals(end.id) || x.equals(start.id)) ) {
                var ordX = todaysOrders.get(x);
                var route = myMapping.getRoute(end.id, ordX.getDestination(),
                        end.coordinates);
                var totalDist = route.size();
                var weight = totalDist / (double) ordX.getDeliveryCost();

                var edge = new tspEdge(weight, route, totalDist);
                g.addEdge(x,end.id,edge);
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
        var x = g.getEdgeSource(Collections.max(g.incomingEdgesOf(end.id)));
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
        var curr = start.id;
        ArrayList<String> removed = new ArrayList<>();

        while (movesUsed > MOVES_ALLOWED || curr.equals(start.id)) {
            var whileGraph  = (SimpleDirectedWeightedGraph<String,tspEdge>) preserveGraph.clone();
            movesUsed = 0;
            curr = start.id;
            perms = new ArrayList<String>();
            flight = new ArrayList<DroneMove>();

            while (hasNextEdge(whileGraph, curr)) {
                var nextEdge = greedyNextEdge(whileGraph, curr);
                movesUsed += nextEdge.getMoves();
                flight.addAll(nextEdge.getRoute());
                var next = whileGraph.getEdgeTarget(nextEdge);
                perms.add(curr);
                whileGraph.removeVertex(curr);
                curr = next;
            }

            addEnd(whileGraph); //final order to end location for day.

            var homeEdge = (whileGraph.getEdge(curr,end.id));
            flight.addAll(homeEdge.getRoute());
            movesUsed += homeEdge.getMoves();
            perms.add(end.id);


            if (movesUsed > MOVES_ALLOWED) { //prepare for next loop
                //get order that is 'worst' from end location, remove from graph that will
                //be reset for use in the next loop

                System.out.println("PERM: "+ perms);
                System.out.println("MOVES USED " + movesUsed);

                var fixEnds  = (SimpleDirectedWeightedGraph<String,tspEdge>) preserveGraph.clone();
                addEnd(fixEnds);
                removed.add(worstEndVert(fixEnds));

                preserveGraph.removeAllVertices(removed);
            }
        }

        this.ordersCompleted = new ArrayList<>();
        this.ordersCompleted.addAll(perms);
        this.ordersCompleted.remove(start.id);
        this.ordersCompleted.remove(end.id);

        this.flightPath = flight;


        /////////////
        System.out.println("//");

        int i = 0;
        for (String s : removed) {
            i+= todaysOrders.get(s).getDeliveryCost();
        }
        System.out.printf("Cost lost %s%n", i);

        int j = 0;
        for (String perm : perms) {
            if (!(perm.equals("START") || perm.equals("END"))) {
                j+= todaysOrders.get(perm).getDeliveryCost();

            }
        }
        System.out.println("total profit $$$ : " + j);
        System.out.println("Monterey shit" + (j/(double) (i+j) ));



        System.out.println("moves " + movesUsed);
        System.out.println(perms.toString());
        DroneMove.getMovesAsFC(flight);

//        for (DroneMove droneMove : flight) {
//            System.out.println(droneMove.toString());
//        }

        System.out.println("MOVES :" + flight.size());
        for (i = 0; i < flight.size() - 1; i++) {
            var dm = flight.get(i);
            var dm2 = flight.get(i + 1);
//            System.out.println(dm);

            if (!(dm.getTo().closeTo(dm2.getFrom()))) {
                System.out.println(dm);
                System.err.println("UH OH!");
                System.out.println(dm2);
            }
        }
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

