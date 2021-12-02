package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

/**
 * Class for methods on movement, such as operating on coordinates.
 */
public class LongLat {

    /** Constant for what is deemed as 'close-to' */
    protected static final double CLOSE_TO_DISTANCE = 0.00015;

    /** Constant for the distance between each move */
    protected static final double STRAIGHT_LINE_DISTANCE = 0.00015;

    /** Minimum angle a Drone can move */
    protected static final int MIN_ANGLE = 0;

    /** Maximum angle a Drone can move (due to 0 index) */
    protected static final int MAX_ANGLE = 350;

    /** Hover move or 'junk' angle */
    protected static final int JUNK_ANGLE = -999;

    /** Interval size between two angles must be a multiple of this. */
    protected static final int ANGLE_INTERVAL = 10;

    /** Confinement North latitude */
    private static final double CONFINEMENT_LATITUDE_NORTH = 55.946233;

    /** Confinement South latitude */
    private static final double CONFINEMENT_LATITUDE_SOUTH = 55.942617;

    /** Confinement West longitude */
    private static final double CONFINEMENT_LONGITUDE_WEST = -3.192473;

    /** Confinement East longitude */
    private static final double CONFINEMENT_LONGITUDE_EAST = -3.184319;


    /** Longitude of this coordinate */
    private final double longitude;

    /** Latitude of this coordinate */
    private final double latitude;

    /**
     * Create a LongLat object by passing values for longitude and latitude respectively.
     *
     * @param longitude a double for longitude
     * @param latitude  a double for latitude
     */
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Checks to ensure the current position is within the allowed area.
     * The confined area is set by four corners in class constants.
     *
     * @return true if it is within the confined area.
     */
    public boolean isConfined() {
        return (this.getLatitude() < CONFINEMENT_LATITUDE_NORTH && this.getLatitude() > CONFINEMENT_LATITUDE_SOUTH &&
                this.getLongitude() > CONFINEMENT_LONGITUDE_WEST && this.getLongitude() < CONFINEMENT_LONGITUDE_EAST);
    }

    /**
     * Calculates the distance between self and a given location using Euclidean Distance.
     *
     * @param destination the 'to' location for which distance is to be calculated.
     * @return the magnitude of the distance in degrees.
     */
    public double distanceTo(LongLat destination) {
        return (Math.sqrt(Math.pow(this.getLongitude() - destination.getLongitude(), 2) +
                Math.pow(this.getLatitude() - destination.getLatitude(), 2)));
    }

    /**
     * Checks if current location is 'close' to a given location.
     * The tolerance is set by a class constant in degrees.
     *
     * @param destination the location for which we want to check if we are close to.
     * @return true if the location is regarded as 'close' to self.
     */
    public boolean closeTo(LongLat destination) {
        return this.distanceTo(destination) < CLOSE_TO_DISTANCE;
    }

    /**
     * Moves to a new location given an angle, or remains stationary by hovering.
     * The amount moved in the direction is set by a class constant in degrees.
     *
     * @param angle if the angle is from 0 to 350 and a multiple of 10 then the drone moves, for -999 it hovers.
     * @return the new location of drone, even if it is hovering.
     * @throws IllegalArgumentException if angle is not within the allowed bounds.
     */
    public LongLat nextPosition(int angle) {
        if (angle >= MIN_ANGLE && angle <= MAX_ANGLE && angle % ANGLE_INTERVAL == 0) {
            double newLongitude = this.getLongitude() + (Math.cos(Math.toRadians(angle)) * STRAIGHT_LINE_DISTANCE);
            double newLatitude = this.getLatitude() + (Math.sin(Math.toRadians(angle)) * STRAIGHT_LINE_DISTANCE);
            return new LongLat(newLongitude, newLatitude);

        } else if (angle == JUNK_ANGLE) {
            return new LongLat(this.getLongitude(), this.getLatitude());
        }
        throw new IllegalArgumentException("Angle given is out of bounds.");
    }

    /**
     * Converts LongLat to a Point
     *
     * @return this LongLat as a Point
     */
    public Point toPoint() {
        return Point.fromLngLat(this.getLongitude(), this.getLatitude());
    }

    /**
     * Calculates the distance between self and a given location using Manhattan Distance.
     *
     * @param destination the 'to' location for which distance is to be calculated.
     * @return the magnitude of the distance in degrees.
     */
    public double manhattanDistanceTo(LongLat destination) {
        var dx = Math.abs(this.getLongitude() - destination.getLongitude());
        var dy = Math.abs(this.getLatitude() - destination.getLatitude());
        return (dx + dy);

    }

    /**
     * A heuristic for distances using weighted Euclidean and Manhattan distances.
     *
     * @param destination the 'to' location for which the heuristic is to be calculated.
     * @return the heuristic calculated.
     */
    public Double flightHeuristic(LongLat destination) {
        var x = 0.55;
        var y = 1.60;
        return x * manhattanDistanceTo(destination) + y * distanceTo(destination);
    }

    /**
     * Defines which method of distance will be used when comparing two stops on TSP.
     *
     * @param target the 'to' location for which the heuristic is to be calculated.
     * @return the heuristic calculated.
     */
    public Double tspHeuristic(LongLat target) {
        return manhattanDistanceTo(target);
    }

    /**
     * Override the 'equals' method so that two LongLats with the same coordinates are equal.
     *
     * @param obj LongLat to compare against
     * @return boolean if they are equal on coordinates.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final LongLat other = (LongLat) obj;
        return this.getLongitude() == other.getLongitude() && this.getLatitude() == other.getLatitude();
    }

    /**
     * Override hashcode due to change in equals
     *
     * @return hashcode for this LongLat
     */
    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (Double.hashCode(getLongitude()));
        result = 31 * result + (Double.hashCode(getLatitude()));
        return result;
    }

    /**
     * Override toString as the longitudes and latitudes.
     *
     * @return string printable representation of LongLat with coordinates.
     */
    @Override
    public String toString() {
        return String.format("%s,%s", this.getLongitude(), this.getLatitude());
    }

    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
    }
}
