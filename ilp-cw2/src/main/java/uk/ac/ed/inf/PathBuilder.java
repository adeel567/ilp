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
                    var distXY = (myMapping.getNumberOfMovesOfRoute(route));
                    var totalDist = distXY + todaysOrders.get(y).totalMovesUsed;
                    var weight = totalDist/(double)todaysOrders.get(y).totalCost;

                    var edge = new tspEdge();
                    edge.weight = weight;
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
                var distXY = (myMapping.getNumberOfMovesOfRoute(route));
                var weight = distXY / (double) todaysOrders.get(y).totalCost;

                var edge = new tspEdge();
                edge.weight = weight;
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
        return Collections.min(g.outgoingEdgesOf(from));
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
        var permutations = new ArrayList<String>();
        permutations.add(start.id);

    }
}
