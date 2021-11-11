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



    public PathBuilder(HashMap<String,Order> todaysOrders) {
        this.todaysOrders = todaysOrders;
        this.start = new Stop("START", new LongLat(AT_LONGITUDE, AT_LATITUDE),"START");
        this.end = new Stop("END", new LongLat(AT_LONGITUDE, AT_LATITUDE),"END");
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
                    var route = myNavigation.getRoute(ordY.getOrderNo(),
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
                var route = myNavigation.getRoute(ordY.getOrderNo(), start.coordinates,
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
                var route = myNavigation.getRoute(end.id, ordX.getDestination(),
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
            perms.add(start.id);
            flight = new ArrayList<DroneMove>();

            while (hasNextEdge(whileGraph, curr)) {
                var nextEdge = greedyNextEdge(whileGraph, curr);
                movesUsed += nextEdge.getMoves();
                flight.addAll(nextEdge.getRoute());
                var next = whileGraph.getEdgeTarget(nextEdge);
                whileGraph.removeVertex(curr);
                curr = next;
                perms.add(next);
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


        System.out.println(perms);
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
            System.out.println(dm);

            if (!(dm.getTo().equals(dm2.getFrom()))) {
//                System.out.println(dm);
//                System.out.println("UH OH!");
//                System.out.println(dm2);
            }
        }
    }

    public void flightFromStopsMade() {
        ArrayList<DroneMove> route = new ArrayList<>();
        ArrayList<Stop> test = allStopsMade();

        //add first journey on its own
        var a = test.get(0);
        var b = test.get(1);
        route.addAll(myNavigation.getRoute(b.orderNo,a.coordinates,b.coordinates));
        var latestC = route.get(route.size()-1).getTo();
        route.add(new DroneMove(b.orderNo,latestC,latestC,LongLat.JUNK_ANGLE));


        for (int i = 1; i < test.size()-1; i++) {
             var x = route.get(route.size()-1).getTo();
             var y = test.get(i+1);
            route.addAll(myNavigation.getRoute(y.orderNo,x,y.coordinates));
             latestC = route.get(route.size()-1).getTo();
            route.add(new DroneMove(y.orderNo,latestC,latestC,LongLat.JUNK_ANGLE));
        }
        this.flightPath = route;




        System.out.println("//");

        System.out.println("MOVES :" + route.size());

        int i;
        for (i = 0; i < route.size() - 1; i++) {
            var dm = route.get(i);
            var dm2 = route.get(i + 1);
               System.out.println(dm);

            if (!(dm.getTo().equals(dm2.getFrom()))) {
                System.out.println(dm);
                System.out.println("UH OH!");
                System.out.println(dm2);
            }
        }

    }

    private ArrayList<Stop> allStopsMade() {
        var jobs = this.getOrdersDelivered();
        var test = new ArrayList<Stop>();

        test.add(start); //calculate all stops made
        for (var job : jobs) {
            test.addAll(job.getAllStops());
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

