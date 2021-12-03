package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for operations on the drone itself, which allows for a flightpath to be created
 * by instructing the drones which moves it shall make.
 */
public class Drone {

    /** Store the path taken by the drone, formatted as DroneMoves */
    private ArrayList<DroneMove> flightPath;

    /** Store the initial location of the Drone when it was launched. */
    private final LongLat initialLocation;

    /** Current location of drone as a LongLat */
    private LongLat currentLocation;

    /** Current order number the drone is considering */
    private String currentOrderNo;

    /** Points to the beginning of each new order. Used for rolling back */
    private final ArrayList<Integer> orderIndexes;

    /** Constant for the order number when a move is made and it's not part of an order */
    private static final String UNKNOWN_ORDER = "UNKNOWN";

    /**
     * Create a drone by providing an initial location in LongLat form.
     *
     * @param initialLocation LongLat of initial position.
     */
    public Drone(LongLat initialLocation) {
        this.initialLocation = initialLocation;
        this.currentLocation = initialLocation;
        this.flightPath = new ArrayList<>();
        this.orderIndexes = new ArrayList<>();
        this.currentOrderNo = UNKNOWN_ORDER;
    }

    /**
     * Set which order the drone is currently delivering.
     * This is required for accounting for the DroneMoves correctly.
     *
     * @param newOrderNo of the order being delivered.
     */
    public void setCurrentOrder(String newOrderNo) {
        if (!currentOrderNo.equals(newOrderNo)) {
            orderIndexes.add(flightPath.size());
            this.currentOrderNo = newOrderNo;
        }
    }

    /**
     * Hover the drone at its current location.
     * Log the move in the flightpath.
     */
    public void doHover() {
        if (currentOrderNo.equals(UNKNOWN_ORDER)) {
            System.err.println("HOVERING WITH NO ORDER");
        }

        var newMove = new DroneMove(currentOrderNo, currentLocation, currentLocation, LongLat.JUNK_ANGLE);
        flightPath.add(newMove);
    }

    /**
     * Do one fly move and update current location.
     * This does *not* check for intersections with No Fly Zone,
     * but does check it is a doable move.
     *
     * @param y     location to move to as LongLat.
     * @param angle angle of move being made.
     */
    private void doMove(LongLat y, int angle) {
        var nextLocation = currentLocation.nextPosition(angle);

        if (Math.abs(currentLocation.distanceTo(nextLocation) - currentLocation.distanceTo(y)) > 1E12) {
            System.err.println("Possible illegal move in doMove: " + y);
        }

        var newMove = new DroneMove(currentOrderNo, currentLocation, y, angle);
        flightPath.add(newMove);
        currentLocation = newMove.getTo();
    }

    /**
     * Construct the route to a stop from current location and fly the drone there.
     * Log the DroneMoves made in the flightpath and update current location.
     *
     * @param dest to fly to.
     */
    public void flyToLocation(LongLat dest) {
        if (currentLocation.closeTo(dest)) {
            doHover(); //if close to dest. then just hover for one move.
        } else {
            var points = new Pathfinding().routeTo(currentLocation, dest);
            this.commitMoves(points);
        }

        if (!currentLocation.closeTo(dest)) {
            System.err.println("LOCATION POTENTIALLY NOT CLOSE TO" + dest);
        }
    }

    /**
     * Construct route from current location to a Stop and fly there.
     * Set order being delivered from Stop given.
     * Log the DroneMoves made in the flightpath and update current location.
     *
     * @param stop to fly to.
     */
    public void flyToStop(Stop stop) {
        setCurrentOrder(stop.getOrderNo());
        flyToLocation(stop.getCoordinates());
    }

    /**
     * Takes the collection of nodes returned by A* or other algorithm and converts into usable DroneMoves
     * which are added to the FlightPath.
     * Checks for any possible issues.
     *
     * @param path collection of A* nodes returned by A* algorithm.
     */
    public void commitMoves(List<PathfindingNode> path) {
        if (path.size() <= 1) {
            System.err.println("Pathfinding returned only position. Possible hover or no path is available");
            System.exit(1);
        }

        for (int i = 0; i < path.size() - 1; i++) {
            var x = path.get(i).asLongLat();
            var y = path.get(i + 1).asLongLat();
            var ang = path.get(i + 1).getAngle();
            this.doMove(y, ang);
        }
    }

    /**
     * Get the real distance to a location from current location.
     * Does not affect current location or current flightpath.
     * @param dest location to fly to.
     * @return int magnitude of DroneMoves to a location.
     */
    public int movesTo(LongLat dest) {
        int dist;

        if (currentLocation.closeTo(dest)) {
            dist = 1;
        } else {
            var points = new Pathfinding().routeTo(currentLocation, dest);
            dist = points.size() - 1; //-1 needed as a line requires at least two points to start with
        }
        return dist;
    }

    /**
     * Deletes all moves made since order number was last changed from one unique value to another.
     * Must be at least one order completed.
     */
    public void rollbackOrder() {
        int indx; //pointer to latest order number.
        if (orderIndexes.size() <= 1) {
            indx = 0;
        } else {
            indx = orderIndexes.get(orderIndexes.size() - 1);
        }

        flightPath = new ArrayList<>(flightPath.subList(0, indx));
        orderIndexes.remove(orderIndexes.size() - 1);

        DroneMove latest;
        if (flightPath.size() == 0) { //if rolling back from the first order, then reset to initial state.
            currentOrderNo = UNKNOWN_ORDER;
            currentLocation = initialLocation;
        } else {
            latest = flightPath.get(flightPath.size() - 1);
            currentOrderNo = latest.getId();
            currentLocation = latest.getTo();
        }
    }

    /**
     * Does a final check to ensure DroneMoves are consistent.
     * This to to ensure that each follows from the next.
     */
    private void validateFlightPath() {
        for (int i = 0; i < flightPath.size() - 1; i++) {
            var x = flightPath.get(i);
            var y = flightPath.get(i + 1);

            if (!(x.getTo().equals(y.getFrom()))) {
                System.err.println("MOVES NOT CONSISTENT");
                System.err.println(x);
                System.err.println(y);

            }
        }
    }

    /**
     * Return flight path created.
     * Outputs to console if there are any irregularities, useful for debugging.
     *
     * @return flight path as DroneMoves collection.
     */
    public ArrayList<DroneMove> getFlightPath() {
        validateFlightPath(); //do a sanity check before returning. Testing reasons only.
        return this.flightPath;
    }

    public LongLat getCurrentLocation() {
        return this.currentLocation;
    }

    public String getCurrentOrderNo() {
        return this.currentOrderNo;
    }

    public int getMovesUsed() {
        return this.flightPath.size();
    }
}
