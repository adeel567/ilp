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
     * Hover the drone at its current location.
     * Log the move in the flightpath.
     */
    public void doHover() {
        var newMove = new DroneMove(currentOrderNo, currentLocation, currentLocation, LongLat.JUNK_ANGLE);
        flightPath.add(newMove);
    }

    /**
     * Do one fly move and update current location.
     * This does *not* check for intersections with No Fly Zone,
     * but does check it is a doable move.
     * @param y location to move to as LongLat.
     * @param angle angle of move being made.
     */
    private void doMove(LongLat y, int angle) {

        var nextLocation = currentLocation.nextPosition(angle);

        if (Math.abs(currentLocation.distanceTo(nextLocation)-currentLocation.distanceTo(y))>1E12) {
            System.err.println("Possible illegal move in doMove: " + y);
        }

        var newMove = new DroneMove(currentOrderNo, currentLocation, y, angle);
        flightPath.add(newMove);
        currentLocation = newMove.getTo();
    }

    /**
     * Construct the route to a stop from current location and fly the drone there.
     * Log the DroneMoves made in the flightpath and update current location.
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
     * @param path collection of A* nodes returned by A* algorithm.
     */
    public void commitMoves(List<PathfindingNode> path) {
        if (path.size()<=1) {
            System.err.println("Pathfinding returned only position. Possible hover or no path is available");
            System.exit(1);
        }

        for (int i = 0; i < path.size() - 1; i++) {
            var x = path.get(i).asLongLat();
            var y = path.get(i + 1).asLongLat();
            var ang = path.get(i + 1).getAngle();
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

    /**
     * Return flight path created.
     * Outputs to console if there are any irregularities, useful for debugging.
     * @return flight path as DroneMoves collection.
     */
    public ArrayList<DroneMove> getFlightPath() {
        validateFlightPath(); //do a sanity check before returning. Testing reasons only.
        return this.flightPath;
    }

    public int getMovesUsed() {
        return this.flightPath.size();
    }
}
