package uk.ac.ed.inf;

import java.util.*;

/**
 * Class for calculating the optimal route between two points.
 */
public class Pathfinding implements PathfindingInterface {

    /** Scale of the increments pathfinding makes. Set lower for precision, but worse performance */
    private static final int PATHFINDING_ANGLE_INCREMENT = 30;

    /** Instance of No-Fly zones to check intersection */
    private static final NoFlyZones myNoFlyZones = NoFlyZones.getInstance();

    /**
     * Uses the A* algorithm to compute a route from a start node to a destination node.
     * Finishes if final location is 'close-to' the destination.
     * The amount of available moves per iteration can be controlled via the angle increment constant.
     * Algorithm based on: https://www.baeldung.com/java-a-star-pathfinding
     *
     * @param start  location to begin at
     * @param target location to end 'close-to'
     * @return the final node, from which the route can be derived.
     */
    private PathfindingNode doAStar(PathfindingNode start, PathfindingNode target) {
        PriorityQueue<PathfindingNode> openList = new PriorityQueue<>();
        PriorityQueue<PathfindingNode> closedList = new PriorityQueue<>();
        HashMap<LongLat, PathfindingNode> all = new HashMap<>();

        start.setG(0);
        start.setF((start.getG() + start.flightHeuristic(target)));
        openList.add(start);
        all.put(start.asLongLat(), start);

        while (!openList.isEmpty()) {
            PathfindingNode n = openList.peek();
            if (n.closeTo(target)) {
                return n;
            }

            for (PathfindingNode m : n.generateNeighbours(PATHFINDING_ANGLE_INCREMENT)) {
                var a = m.toPoint();
                var b = n.toPoint();

                if (!myNoFlyZones.doesIntersectNoFly(a, b) && m.isConfined()) {
                    double totalWeight = (n.getG() + LongLat.STRAIGHT_LINE_DISTANCE);

                    if (all.containsKey(m.asLongLat())) { //as DS is not a graph, need to check if node is new or not.
                        m = all.get(m.asLongLat());
                    } else {
                        all.put(m.asLongLat(), m);
                    }

                    if (!openList.contains(m) && !closedList.contains(m)) {
                        m.setParent(n);
                        m.setG(totalWeight);
                        m.setF((m.getG() + m.flightHeuristic(target)));
                        openList.add(m);

                    } else {
                        if (totalWeight < m.getG()) {
                            m.setParent(n);
                            m.setG(totalWeight);
                            m.setF((m.getG() + m.flightHeuristic(target)));

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
     * Computes the best route from one LongLat to another.
     * Internally uses the A star algorithm.
     * Note: the start and end should be checked that they are not 'close'.
     * otherwise a Hover move is all that is needed.
     *
     * @param startLL location to start at
     * @param endLL   location to end 'close-to'
     * @return a collection of Pathfinding nodes.
     */
    public List<PathfindingNode> routeTo(LongLat startLL, LongLat endLL) {
        if (startLL.closeTo(endLL)) {
            System.err.println("WARNING: START AND END ARE CLOSE");
        }

        PathfindingNode start = new PathfindingNode(startLL.getLongitude(), startLL.getLatitude());
        PathfindingNode end = new PathfindingNode(endLL.getLongitude(), endLL.getLatitude());

        PathfindingNode n = doAStar(start, end);

        List<PathfindingNode> path = new ArrayList<>();
        while (n.getParent() != null) {
            path.add(n);
            n = n.getParent();
        }
        path.add(n);
        Collections.reverse(path);

        return path;
    }
}
