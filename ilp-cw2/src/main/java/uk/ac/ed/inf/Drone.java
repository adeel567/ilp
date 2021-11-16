package uk.ac.ed.inf;

import java.util.*;

/**
 * Class for operations on the drone itself, which allows for a flightpath to be created
 * by instructing the drones which moves it shall make.
 */
public class Drone {

    private final ArrayList<DroneMove> flightPath;
    private LongLat currentLocation;
    private String currentOrderNo;

    private final NoFlyZones myNoFlyZones = NoFlyZones.getInstance();

    private static final int PATHFINDING_ANGLE_INCREMENT = 30;

    /**
     * Create a drone by providing an initial location in LongLat form.
     * @param initialLocation LongLat of initial position.
     */
    public Drone(LongLat initialLocation){
        this.currentLocation = initialLocation;
        this.flightPath = new ArrayList<>();
    }

    /**
     * Set which order the drone is currently delivering.
     * This is required for accounting for the DroneMoves correctly.
     * @param orderNo of the order being delivered.
     */
    public void setCurrentOrder(String orderNo) {
        this.currentOrderNo = orderNo;
    }

    /**
     * Construct the route to a stop from current location and fly the drone there.
     * Log the DroneMoves made in the flightpath.
     * @param stop to fly to.
     */
    public void flyToStop(Stop stop) {
        flyTo(currentLocation,stop.getCoordinates());

        if (!currentLocation.closeTo(stop.getCoordinates())) {
            System.err.println("LOCATION NOT CLOSE TO" + stop.getCoordinates());
        }
    }

    /**
     * Hover the drone at its current location.
     * Log the move in the flightpath.
     */
    public void doHover() {
        var newMove = new DroneMove(currentOrderNo, currentLocation, currentLocation, LongLat.JUNK_ANGLE);
        flightPath.add(newMove);
    }

    /**
     * Move to a location.
     * This does *not* check for intersections with No Fly Zone,
     * but does check it is a doable move.
     * @param y location to move to as LongLat.
     * @param angle angle of move being made.
     */
    public void doMove(LongLat y, int angle) {

        var nextLocation = currentLocation.nextPosition(angle);
        var newMove = new DroneMove(currentOrderNo, currentLocation, y, angle);
        flightPath.add(newMove);
        currentLocation = newMove.getTo();
    }

    /**
     * Uses the A* algorithm to compute a route from a start node to a destination node.
     * Finishes if final location is 'close-to' the destination.
     * The amount of available moves per iteration can be controlled via the angle increment constant.
     * Algorithm based on: https://www.baeldung.com/java-a-star-pathfinding
     * @param start location to begin at
     * @param target location to end 'close-to'
     * @return the final node, from which the route can be derived.
     */
    private aStarNode doAStar(aStarNode start, aStarNode target) {
        PriorityQueue<aStarNode> openList = new PriorityQueue<>();
        PriorityQueue<aStarNode> closedList = new PriorityQueue<>();
        HashMap<LongLat,aStarNode> all = new HashMap<>();

        start.g = 0;
        start.f = (start.g + start.flightHeuristic(target));
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

                if (!myNoFlyZones.doesIntersectNoFly(a, b) && m.isConfined()) {
                    double totalWeight = (n.g + LongLat.STRAIGHT_LINE_DISTANCE);

                    if (all.containsKey(m.asLongLat())) {
                        m = all.get(m.asLongLat());
                    } else {
                        all.put(m.asLongLat(),m);
                    }

                    if (!openList.contains(m) && !closedList.contains(m)) {
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = (m.g + m.flightHeuristic(target));
                        openList.add(m);

                    } else {
                        if (totalWeight < m.g) {
                            m.parent = n;
                            m.g = totalWeight;
                            m.f = (m.g + m.flightHeuristic(target));

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

    /**
     * Moves the drone from a start point to the end by calculating the best route it can.
     * Logs the route taken in the flightpath
     * @param startLL location to start at
     * @param endLL location to end 'close-to'
     */
    private void flyTo(LongLat startLL, LongLat endLL) {
        if (startLL.closeTo(endLL)) {
            doHover();
        } else {

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

            commitMoves(path);
        }
    }

    /**
     * Takes the collection of nodes returned by A* and converts into usable DroneMoves
     * which are added to the FlightPath. Checks for any possible issues.
     * @param path collection of A* nodes returned by A* algorithm.
     */
    private void commitMoves(List<aStarNode> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            var x = path.get(i).asLongLat();
            var y = path.get(i + 1).asLongLat();
            var ang = path.get(i+1).angle;

            var expected = x.nextPosition(ang);

            var bad = (Math.abs(x.distanceTo(y) - x.distanceTo(expected))) >1E-12;
            if (bad) {
                System.out.println("Pathfinding move was illegal");
                System.out.println("from " + x);
                System.out.println("to: " + y);
                System.out.println("ang: " + ang);
                System.out.println("expected y" + expected);
                System.out.println(x.distanceTo(y));
                System.out.println(x.distanceTo(expected));
                System.out.println(x.distanceTo(y) - x.distanceTo(expected));
            }

            this.doMove(y,ang);
        }
    }

    /**
     * Does a final check to ensure DroneMoves are consistent.
     * This to to ensure that each follows from the next.
     */
    private void validateFlightPath() {
        for (int i = 0; i < flightPath.size()-1; i++) {
            var x = flightPath.get(i);
            var y = flightPath.get(i+1);

            if (!(x.getTo().equals(y.getFrom()))) {
                System.err.println("MOVES NOT CONSISTENT");
                System.err.println(x);
                System.err.println(y);

            }
        }
    }

    public ArrayList<DroneMove> getFlightPath() {
        validateFlightPath();
        return this.flightPath;
    }

    public int movesUsed() {
        return this.flightPath.size();
    }
}
