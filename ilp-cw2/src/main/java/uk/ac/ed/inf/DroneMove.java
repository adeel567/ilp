package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores every move the drone makes in a format
 * as required for the database.
 */
public class DroneMove {
    private final LongLat from;
    private final LongLat to;
    private final int angle;
    private final String id;

    /**
     * Create DroneMove from values as would be required in the output.
     * @param id order being delivered
     * @param from LongLat of from location.
     * @param to LongLat of to location.
     * @param angle angle taken to reach to location.
     */
    public DroneMove(String id, LongLat from, LongLat to, int angle) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.angle = angle;

        if(angle == LongLat.JUNK_ANGLE && (!(from.equals(to)))) {
            System.err.println("DRONE HOVER ILLEGAL");
            System.err.println(this);

        } else if (((from.distanceTo(to) > LongLat.STRAIGHT_LINE_DISTANCE+1E-12) ||
            (from.distanceTo(to) < LongLat.STRAIGHT_LINE_DISTANCE-1E-12)) && angle!=LongLat.JUNK_ANGLE) {
            System.err.println("DRONE MOVE POTENTIALLY ILLEGAL");
            System.err.println(this);
            System.err.println(from.distanceTo(to));
        }

    }

    public LongLat getFrom() {
        return from;
    }

    public LongLat getTo() {
        return to;
    }

    public String getId() {
        return this.id;
    }

    public int getAngle() {
        return this.angle;
    }

    /**
     * Override toString to be all relevant variables.
     * @return String containging: orderNo, to coord, from coord, angle.
     */
    @Override
    public String toString() {
        return (String.format("Job: %s, from: %s, to: %s, angle: %s", this.id, this.from,this.to,this.angle));
    }

    /**
     * Takes a collection of DroneMoves and converts them into a
     * collection of GEOJson Points.
     * @param dms a collection of DroneMoves
     * @return a collection of Points
     */
    private static ArrayList<Point> movesToPath(ArrayList<DroneMove> dms) {
        var points = new ArrayList<Point>();
        points.add(dms.get(0).getFrom().toPoint());
        points.add(dms.get(0).getTo().toPoint());

        if (dms.size() > 1) {
            for (int i = 1, dmsSize = dms.size(); i < dmsSize; i++) {
                DroneMove dm = dms.get(i);
                points.add(dm.getTo().toPoint());
            }
        }
        return points;
    }

    /**
     * Takes a collection of Points and turns them into a FeatureCollection
     * @param path a collection of Points
     * @return a FeatureCollection of the given Points
     */
    private static FeatureCollection getRouteAsFC(List<Point> path) {
        var feature = Feature.fromGeometry(
                (Geometry) LineString.fromLngLats(path));

        return FeatureCollection.fromFeature(feature);
    }

    /**
     * Takes a collection of DroneMoves and turns them into a FeatureCollection.
     * @param dms a collection of DroneMoves.
     * @return a FeatureCollection of the given DroneMoves.
     */
    public static FeatureCollection getMovesAsFC(ArrayList<DroneMove> dms) {
        var x = movesToPath(dms);
        return getRouteAsFC(x);
    }
}
