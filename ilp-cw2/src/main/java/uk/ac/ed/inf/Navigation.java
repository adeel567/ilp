package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.util.*;

public class Navigation {

    private static final String SERVER_PATH_TO_NFZ = "buildings/no-fly-zones.geojson";
    private static final int PATHFINDING_ANGLE_INCREMENT = 30;

    private static Navigation instance = null;

    private FeatureCollection NoFlyZones;
    private ArrayList<Polygon> NoFlyZonesPoly;

    private Navigation() {
        this.NoFlyZones = fetchNoFlyZones();
        this.NoFlyZonesPoly = fetchNoFlyZonesPolygons();
    }

    public static Navigation getInstance() {
        if (instance == null) {
            instance = new Navigation();
        }
        return instance;
    }

    private FeatureCollection fetchNoFlyZones() {
        String nfzURL = ServerIO.URLFromPath(SERVER_PATH_TO_NFZ);
        String response = ServerIO.getRequest(nfzURL); //get an unparsed response from server

        return FeatureCollection.fromJson(response);
    }

    private ArrayList<Polygon> fetchNoFlyZonesPolygons() {
        assert this.NoFlyZones.features() != null;
        ArrayList<Polygon> polys = new ArrayList<>(); //add all polygons to arraylist
        for (var feature : this.NoFlyZones.features()) {
            polys.add((Polygon) feature.geometry());
        }
        return polys;
    }


    public boolean doesIntersectNoFly(Point start, Point destination) {

        var lineToCheck = new Line2D.Double(start.longitude(), start.latitude(),
                destination.longitude(), destination.latitude());

        for (var x : this.NoFlyZonesPoly) {
            for (int i = 0; i < x.coordinates().get(0).size() - 1; i++) {

                var polyLine = new Line2D.Double(
                        x.coordinates().get(0).get(i).longitude(),
                        x.coordinates().get(0).get(i).latitude(),
                        x.coordinates().get(0).get(i + 1).longitude(),
                        x.coordinates().get(0).get(i + 1).latitude());

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
        HashMap<LongLat,aStarNode> all = new HashMap<>();

        start.g = 0;
        start.f = (start.g + start.heuristic(target));
        openList.add(start);
        all.put(start.asLongLat(),start);

        while (!openList.isEmpty()) {
            aStarNode n = openList.peek();
            if (n.closeTo(target)) {
                return n;
            }

            for (aStarNode m : n.generateNeighbours(PATHFINDING_ANGLE_INCREMENT)) {
                var a = m.toPoint();
                var b = n.toPoint();

                if (!doesIntersectNoFly(a, b) && m.isConfined()) {
                    double totalWeight = (n.g + LongLat.STRAIGHT_LINE_DISTANCE);

                    if (all.containsKey(m.asLongLat())) {
                        m = all.get(m.asLongLat());
                    } else {
                        all.put(m.asLongLat(),m);
                    }

                    if (!openList.contains(m) && !closedList.contains(m)) {
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = (m.g + m.heuristic(target));
                        openList.add(m);

                    } else {
                        if (totalWeight < m.g) {
                            m.parent = n;
                            m.g = totalWeight;
                            m.f = (m.g + m.heuristic(target));

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
        System.err.println("PATH COULD NOT BE FOUND");
        return null;
    }

    public ArrayList<DroneMove> getRoute(String job, LongLat startLL, LongLat endLL) {
        if (startLL.closeTo(endLL)) { //if already close then hover.
            var x = new DroneMove(job, startLL, startLL, LongLat.JUNK_ANGLE);
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
        return pathToMoves(path, job);
    }

    private ArrayList<DroneMove> pathToMoves(List<aStarNode> path, String job) {
        ArrayList<DroneMove> moves = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            var x = path.get(i).asLongLat();
            var y = path.get(i + 1).asLongLat();
            var ang = path.get(i + 1).angle;
            moves.add(new DroneMove(job, x, y, ang));
        }
        return moves;
    }
}
