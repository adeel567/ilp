package uk.ac.ed.inf;

import java.util.*;
import java.util.List;

public class Pathfinding {
    private static final int PATHFINDING_ANGLE_INCREMENT = 30;
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
    private static PathfindingNode doAStar(PathfindingNode start, PathfindingNode target) {
        PriorityQueue<PathfindingNode> openList = new PriorityQueue<>();
        PriorityQueue<PathfindingNode> closedList = new PriorityQueue<>();
        HashMap<LongLat, PathfindingNode> all = new HashMap<>();

        start.g = 0;
        start.f = (start.g + start.flightHeuristic(target));
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
                    double totalWeight = (n.g + LongLat.STRAIGHT_LINE_DISTANCE);

                    if (all.containsKey(m.asLongLat())) { //as DS is not a graph, need to check if node is new or not.
                        m = all.get(m.asLongLat());
                    } else {
                        all.put(m.asLongLat(), m);
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
     * Computes the best route from one LongLat to another.
     * Internally uses the A star algorithm.
     * Note: the start and end must be checked that they are not 'close'.
     * @param startLL location to start at
     * @param endLL   location to end 'close-to'
     * @return a collection of
     */
    public static List<PathfindingNode> routeTo(LongLat startLL, LongLat endLL) {
        assert !startLL.closeTo(endLL) : "Start and end are close";

        PathfindingNode start = new PathfindingNode(startLL.longitude, startLL.latitude);
        PathfindingNode end = new PathfindingNode(endLL.longitude, endLL.latitude);

        PathfindingNode n = doAStar(start, end);

        List<PathfindingNode> path = new ArrayList<>();
        while (n.parent != null) {
            path.add(n);
            n = n.parent;
        }
        path.add(n);
        Collections.reverse(path);

        return path;
        }
}
