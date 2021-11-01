package uk.ac.ed.inf;

import org.jgrapht.graph.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PathBuilder {

    private HashMap<String,Order> todaysOrders;
    private final Mapping myMapping = new Mapping("localhost", "9898");
    //private Graph tspGraph;
    private SimpleDirectedWeightedGraph<String,tspEdge> realGraph;

    private final Stop start = new Stop("start", new LongLat(-3.186874, 55.944494));
    private final Stop end = new Stop("end", new LongLat(-3.186874, 55.944494));


    public PathBuilder(HashMap<String,Order> todaysOrders) {
        this.todaysOrders = todaysOrders;
    }

    public void buildNodes(){
        var realGraph = new SimpleDirectedWeightedGraph<String, tspEdge>(tspEdge.class);

        for (var orderNo : todaysOrders.keySet()) {
            realGraph.addVertex(orderNo);
        }

        for (var x: realGraph.vertexSet()) {
            for (var y: realGraph.vertexSet()) {
                if (!x.equals(y)) {

                    var route = myMapping.getRoute(todaysOrders.get(x).getDestination(),
                            todaysOrders.get(y).getStart());
                    route.addAll(todaysOrders.get(y).getFlightPath());
                    var totalDist = (myMapping.getNumberOfMovesOfRoute(route));
                   // var totalDist = distXY + todaysOrders.get(y).getMovesUsed();
                    var weight = totalDist/(double) todaysOrders.get(y).getDeliveryCost();

                    var edge = new tspEdge();
                    edge.weight = weight;
                    edge.moves = totalDist;
                    edge.route = route;

                    realGraph.addEdge(x,y,edge);
                    realGraph.setEdgeWeight(realGraph.getEdge(x,y),weight);
                }
            }
        }

        start.id = "START";
        end.id = "END";

        realGraph.addVertex(start.id);
        for (var y : realGraph.vertexSet()) {
            if (!y.equals(start.id)) {
                var route = myMapping.getRoute(start.coordinates,
                        todaysOrders.get(y).getStart());
                route.addAll(todaysOrders.get(y).getFlightPath());
                var totalDist = (myMapping.getNumberOfMovesOfRoute(route));
                var weight = totalDist / (double) todaysOrders.get(y).getDeliveryCost();


                var edge = new tspEdge();
                edge.weight = weight;
                edge.moves = totalDist;
                edge.route = route;

                realGraph.addEdge(start.id, y, edge);
                realGraph.setEdgeWeight(realGraph.getEdge(start.id, y), weight);
            }
        }

        this.realGraph = realGraph;
    }

    private Boolean hasNextEdge (SimpleDirectedWeightedGraph<String,tspEdge> g, String vert) {
        return g.outDegreeOf(vert) > 0;
    }

    private tspEdge greedyNextEdge(SimpleDirectedWeightedGraph<String,tspEdge> g, String from) {
        return (Collections.min(g.outgoingEdgesOf(from)));
    }

    private String worstEndVert(SimpleDirectedWeightedGraph<String,tspEdge> g) {
        var x = g.getEdgeSource(Collections.max(g.incomingEdgesOf(end.id)));
        System.out.println(x);
        return x;
    }


    private int tourSize(SimpleDirectedWeightedGraph<String,tspEdge> g, ArrayList<String> vertexes) {
        assert vertexes.size() > 1  : "tour is of size <=1";
        int i = 0;
        for (int j = 0, vertexesSize = vertexes.size()-1; j < vertexesSize; j++) {
            i += g.getEdge(vertexes.get(j),vertexes.get(j+1)).moves;
        }
        return i;
    }

////    public void buildMatrix() {
//        this.tspGraph = new Graph(this.todaysOrders.size());
//
//        for (var x : todaysOrders) {
//            for (var y : todaysOrders) {
//                var from = todaysOrders.indexOf(x);
//                var to = todaysOrders.indexOf(y);
//                if (!x.equals(y)) {
//                    var route = myMapping.getRoute(x.destination, y.getStart());
//                    var distY = y.totalMovesUsed;
//                    var distXtoY = myMapping.getNumberOfMovesOfRoute(route);
//                    var weight = (distY + distXtoY) / (double) y.totalCost;
//                    tspGraph.initEdges(from, to, weight);
//                    tspGraph.addMoves(from, to, (distY + distXtoY));
//                    tspGraph.addRoutes(from, to, route);
//                } else {
//                    tspGraph.initEdges(from, to, Double.MAX_VALUE);
//                }
//            }
//        }
//
//    }



//    public void doThang() {
//        int movesAvail = 1500;
//        int movesUsed = 0;
//
//        ArrayList<Integer> path = new ArrayList<>();
//
//        ArrayList<Double> ATtoStartsWeight = new ArrayList<>();
//        ArrayList<Integer> ATtoStartsMoves = new ArrayList<>();
//        ArrayList<List<Point>> ATtoStartsRoutes = new ArrayList<>();
//        for (var x: todaysOrders) {
//            var route = myMapping.getRoute(this.AT.location, x.getStart());
//            var distATtoS = myMapping.getNumberOfMovesOfRoute(route);
//            var weight = distATtoS/(double) x.totalCost;
//            ATtoStartsMoves.add(distATtoS);
//            ATtoStartsWeight.add(weight);
//            ATtoStartsRoutes.add(route);
//        }
//
//        ArrayList<Integer> EndtoATMoves = new ArrayList<>();
//        ArrayList<List<Point>> EndtoATRoutes = new ArrayList<>();
//        for (var x: todaysOrders) {
//            var route = myMapping.getRoute(x.destination, this.AT.location);
//            var distATtoS = myMapping.getNumberOfMovesOfRoute(route);
//            EndtoATMoves.add(distATtoS);
//            EndtoATRoutes.add(route);
//        }
//
//        var startIndex = ATtoStartsWeight.indexOf(Collections.min(ATtoStartsWeight));
//
//        path.add(startIndex);
//        tspGraph.setVisited(startIndex);
//        movesAvail -= ATtoStartsMoves.get(startIndex);
////        System.out.println(movesAvail);
////        myMapping.getRouteAsFC(ATtoStartsRoutes.get(startIndex));
////        System.out.println(ATtoStartsMoves.size());
////        System.out.println(todaysOrders.get(startIndex).orderNo);
//
//
//        }
    //        realGraph.addVertex("yeet");
//        var localGraph = (SimpleDirectedWeightedGraph<String, tspEdge>) realGraph.clone();
//        realGraph.removeVertex("yeet");
//        realGraph.addVertex("yeet");
//        System.out.println(localGraph.containsVertex("yeet"));

    public void getTour() {
        var movesAllowed = 1500;

        var g  = (SimpleDirectedWeightedGraph<String,tspEdge>) realGraph.clone();
        var movesUsed = 0;
        ArrayList<String> perms = null;
        ArrayList<DroneMove> flight = null;
        var curr = start.id;
        ArrayList<String> removed = new ArrayList<>();

        while (movesUsed > movesAllowed || curr.equals(start.id)) {
            movesUsed = 0;
            curr = start.id;
            perms = new ArrayList<String>();
            flight = new ArrayList<DroneMove>();

            while (hasNextEdge(g, curr)) {
                var nextEdge = greedyNextEdge(g, curr);
                movesUsed += nextEdge.moves;
                flight.addAll(nextEdge.route);
                var next = g.getEdgeTarget(nextEdge);
                perms.add(curr);
                g.removeVertex(curr);
                curr = next;
            }

            addEnd(g);

            var x = (g.getEdge(curr,end.id));
            var y = x.route;
            flight.addAll(y);
            movesAllowed += x.moves;
            perms.add(end.id);


            if (movesUsed > movesAllowed) { //prepare for next loop
                g  = (SimpleDirectedWeightedGraph<String,tspEdge>) realGraph.clone();
                addEnd(g);
                removed.add(worstEndVert(g));
                g  = (SimpleDirectedWeightedGraph<String,tspEdge>) realGraph.clone();
                g.removeAllVertices(removed);
                System.out.println("MOVES USED " + movesUsed);
            }
        }

        /////////////


        int i = 0;
        for (String s : removed) {
            i+= todaysOrders.get(s).getDeliveryCost();
        }
        System.out.printf("Cost lost %s%n", i);




        System.out.println("moves " + movesUsed);
        System.out.println(perms.toString());
        myMapping.getRouteAsFC(myMapping.movesToPath(flight));

//        for (DroneMove droneMove : flight) {
//            System.out.println(droneMove.toString());
//        }

        for (i = 0; i < flight.size() - 1; i++) {
            var dm = flight.get(i);
            var dm2 = flight.get(i + 1);
//            System.out.println(dm);

            if (!(dm.getTo().closeTo(dm2.getFrom()))) {
                System.out.println(dm.toString());
                System.err.println("UH OH!");
                System.out.println(dm2.toString());
            }
        }
    }

    private void addEnd(SimpleDirectedWeightedGraph<String, tspEdge> g) {
        g.addVertex(end.id);
        for (var y : g.vertexSet()) {
            if (!(y.equals(end.id) || y.equals(start.id)) ) {
                var route = myMapping.getRoute(todaysOrders.get(y).getDestination(),
                        end.coordinates);
                var distXY = (myMapping.getNumberOfMovesOfRoute(route));
                var weight = distXY / (double) todaysOrders.get(y).getDeliveryCost();
               // System.out.println("YOTE " + weight);

                var edge = new tspEdge();
                edge.weight = weight;
                edge.moves = distXY;
                edge.route = route;

                g.addEdge(y, end.id, edge);
                g.setEdgeWeight(g.getEdge(y, end.id), weight);
            }
        }
    }

}
