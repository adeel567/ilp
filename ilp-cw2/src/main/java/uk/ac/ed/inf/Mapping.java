package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Mapping {

    private final String serverName;
    private final String serverPort;
    private FeatureCollection NoFlyZones;
    private ArrayList<Polygon> NoFlyZonesPoly;

    public Mapping(String serverName, String serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        fetchNoFlyZones();
    }

    private void fetchNoFlyZones() {
        String nfzURL = String.format("http://%s:%s/%s", "localhost", "9898", "buildings/no-fly-zones.geojson");
        String response = ServerIO.getRequest(nfzURL); //get an unparsed response from server

        FeatureCollection fc = FeatureCollection.fromJson(response);
        this.NoFlyZones = fc;

        assert fc.features() != null;
        ArrayList<Polygon> polys = new ArrayList<>(); //add all polygons to arraylist
        for (var feature : fc.features()) {
            polys.add((Polygon) feature.geometry());
        }
        this.NoFlyZonesPoly = polys;
    }

//    public boolean isInNoFly(LongLat destination) { //check if point falls within nofly
//        for (Polygon poly : this.NoFlyZonesPoly) {
//            if (TurfJoins.inside(destination.toPoint(), (com.mapbox.geojson.Polygon) poly)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean doesIntersectNoFly(Point start, Point destination) {

        var lineToCheck = new Line2D.Double(start.longitude(), start.latitude(),
                destination.longitude(), destination.latitude());

        for (var x : this.NoFlyZonesPoly) {
            for (int i=0; i<x.coordinates().get(0).size()-1;i++) {

                var polyLine = new Line2D.Double(
                        x.coordinates().get(0).get(i).longitude(),
                        x.coordinates().get(0).get(i).latitude(),
                        x.coordinates().get(0).get(i+1).longitude(),
                        x.coordinates().get(0).get(i+1).latitude());

                if (polyLine.intersectsLine(lineToCheck) || lineToCheck.intersectsLine(polyLine)) {
                    return true;
                }
            }
        }
        return false;
    }


    private aStarNode doAStar(aStarNode start, aStarNode target) {
        PriorityQueue<aStarNode> openList = new PriorityQueue<>();
        PriorityQueue<aStarNode> closedList = new PriorityQueue<>();

        start.f = start.g + start.distanceTo(target);
        openList.add(start);

        while (!openList.isEmpty()) {
            aStarNode n = openList.peek();
            if (n.closeTo(target)) {
                return n;
            }

            int inc = 60;
            for (aStarNode m : n.generateNeighbours(inc)) {
                var a = n.toPoint();
                var b = m.toPoint();

                if (!doesIntersectNoFly(a,b) && m.isConfined()) {
                    double totalWeight = n.g + n.distanceTo(m);

                    if (!openList.contains(m) && !closedList.contains(m)) {
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = m.g + m.distanceTo(target);
                        openList.add(m);
                    } else {
                        if (totalWeight < m.g) {
                            m.parent = n;
                            m.g = totalWeight;
                            m.f = m.g + m.distanceTo(target);

                            if (closedList.contains(m)) {
                                closedList.remove(m);
                                openList.add(m);
                            }
                        }
                    }
                }
            }
            openList.remove(n);
            closedList.add(n);
        }
        return null;
    }

    public ArrayList<DroneMove> getRoute(LongLat startLL, LongLat endLL) {
//        assert !startLL.closeTo(endLL) : "start is close to end, no path";
        if (startLL.closeTo(endLL)) {
            var x = new DroneMove(startLL, startLL, -999);
            var y = new ArrayList<DroneMove>();
            y.add(x);
             return y;
        }

        aStarNode start = new aStarNode(startLL.longitude, startLL.latitude);
        aStarNode end = new aStarNode(endLL.longitude, endLL.latitude);

        aStarNode n = this.doAStar(start, end);

        List<aStarNode> path = new ArrayList<>();
        while (n.parent != null) {
            path.add(n);
            n = n.parent;
        }
        path.add(n);
        Collections.reverse(path);
//        System.out.println("YET");
//      //  System.out.println(path.size());
        return pathToMoves(path);
    }

    private ArrayList<DroneMove> pathToMoves(List<aStarNode> path) {
        ArrayList<DroneMove> moves = new ArrayList<>();
        for (int i = 0; i < path.size()-1; i++) {
            var x = path.get(i).asLongLat();
            var y = path.get(i+1).asLongLat();
            var ang = path.get(i+1).angle;
            moves.add(new DroneMove(x,y,ang));
        }
        return moves;
    }

    public ArrayList<Point> movesToPath(ArrayList<DroneMove> dms) {
        var lls = new ArrayList<Point>();
        lls.add(dms.get(0).getFrom().toPoint());
        lls.add(dms.get(0).getTo().toPoint());

        if (dms.size() > 1) {
            for (int i = 1, dmsSize = dms.size(); i < dmsSize; i++) {
                DroneMove dm = dms.get(i);
//                lls.add(dm.getFrom().toPoint());
                lls.add(dm.getTo().toPoint());
            }
        }
        System.out.println("points " + lls.size());
        return lls;
    }


    public FeatureCollection getRouteAsFC(List<Point> path) {
        var y = Feature.fromGeometry(
                (Geometry) LineString.fromLngLats(path));
      //  System.out.println(y.toJson());

        var x = FeatureCollection.fromFeature(y);
        System.out.println(x.toJson());
        return x;
    }


    public int getNumberOfMovesOfRoute(List<DroneMove> points) {
        return (points.size());
    }
}
