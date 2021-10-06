package uk.ac.ed.inf;

public class LongLat {


    //four corners of the confinement area as constants
    private static final double CONFINEMENT_LATITUDE_NORTH = 55.946233;
    private static final double CONFINEMENT_LATITUDE_SOUTH = 55.942617;
    private static final double CONFINEMENT_LONGITUDE_WEST = -3.192473;
    private static final double CONFINEMENT_LONGITUDE_EAST = -3.184319;

    //constants for different distances required
    private static final double CLOSE_TO_DISTANCE = 0.00015;
    private static final double STRAIGHT_LINE_DISTANCE = 0.00015;

    //constants for angles
    private static final int MIN_ANGLE = 0;
    private static final int MAX_ANGLE = 350;
    private static final int JUNK_ANGLE = -999;
    private static final int ANGLE_INTERVAL = 10;


    /** Stores the longitude of current location */
    public double longitude;
    /** Stores the latitude of current location */
    public double latitude;

    /**
     * Create a LongLat object by passing two values for longitude and latitude respectively.
     * @param longitude a double for longitude
     * @param latitude a double for latitude
     */
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Checks to ensure the current position is within the allowed area.
     * The confined area is set by four corners in class constants.
     * @return true if it is within the confined area.
     */
    public boolean isConfined() {
        return (this.latitude < CONFINEMENT_LATITUDE_NORTH && this.latitude > CONFINEMENT_LATITUDE_SOUTH &&
                this.longitude > CONFINEMENT_LONGITUDE_WEST && this.longitude < CONFINEMENT_LONGITUDE_EAST);
    }

    /**
     * Calculates the distance between self and a given location.
     * @param destination the 'to' location for which distance is to be calculated.
     * @return the magnitude of the distance in degrees.
     */
    public double distanceTo(LongLat destination) {
        if (destination == null) {
            throw new NullPointerException("Destination cannot be null");
        }

        return (Math.sqrt(Math.pow(this.longitude - destination.longitude, 2) +
                Math.pow(this.latitude - destination.latitude, 2)));
    }

    /**
     * Checks if current location is 'close' to a given location.
     * The tolerance is set by a class constant in degrees.
     * @param destination the location for which we want to check if we are close to.
     * @return true if the location is regarded as 'close' to self.
     */
    public boolean closeTo(LongLat destination) {
        if (destination == null) {
            throw new NullPointerException("Destination cannot be null");
        }

        return this.distanceTo(destination) < CLOSE_TO_DISTANCE;
    }

    /**
     * Moves to a new location given an angle, or remains stationary by hovering.
     * The amount moved in the direction is set by a class constant in degrees.
     * @param angle if the angle is from 0 to 350 and a multiple of 10 then the drone moves, for -999 it hovers.
     * @return the new location of drone, even if it is hovering.
     */
    public LongLat nextPosition(int angle) {
        if (angle >= MIN_ANGLE && angle <= MAX_ANGLE && angle % ANGLE_INTERVAL == 0) {
            double newLongitude = this.longitude + (Math.cos(Math.toRadians(angle)) * STRAIGHT_LINE_DISTANCE);
            double newLatitude = this.latitude + (Math.sin(Math.toRadians(angle)) * STRAIGHT_LINE_DISTANCE);
            return new LongLat(newLongitude, newLatitude);

        } else if (angle == JUNK_ANGLE){
            return new LongLat(this.longitude, this.latitude);
        }
        throw new IllegalArgumentException("Angle given is out of bounds.");
    }
}
