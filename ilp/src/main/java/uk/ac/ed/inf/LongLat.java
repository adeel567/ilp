package uk.ac.ed.inf;

public class LongLat {

    private final double CONFINEMENT_LATITUDE_NORTH = 55.946233;
    private final double CONFINEMENT_LATITUDE_SOUTH = 55.942617;
    private final double CONFINEMENT_LONGITUDE_WEST = -3.192473;
    private final double CONFINEMENT_LONGITUDE_EAST = -3.184319;

    private final double CLOSE_TO_DISTANCE = 0.00015;
    private final double STRAIGHT_LINE_DISTANCE = 0.00015;

    public double longitude;
    public double latitude;

    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined() {
        return (this.latitude < CONFINEMENT_LATITUDE_NORTH && this.latitude > CONFINEMENT_LATITUDE_SOUTH &&
                this.longitude > CONFINEMENT_LONGITUDE_WEST && this.longitude < CONFINEMENT_LONGITUDE_EAST);
    }

    public double distanceTo(LongLat destination) {
        return (Math.sqrt(Math.pow(this.longitude - destination.longitude, 2) +
                Math.pow(this.latitude - destination.latitude, 2)));
    }

    public boolean closeTo(LongLat destination) {
        return this.distanceTo(destination) < CLOSE_TO_DISTANCE;
    }

    public LongLat nextPosition(int angle) {
        if (angle >= 0 && angle <= 350 && angle % 10 == 0) {
            double newLongitude = this.longitude + (Math.cos(Math.toRadians(angle)) * STRAIGHT_LINE_DISTANCE);
            double newLatitude = this.latitude + (Math.sin(Math.toRadians(angle)) * STRAIGHT_LINE_DISTANCE);
            return new LongLat(newLongitude, newLatitude);

        } else {
            return new LongLat(this.longitude, this.latitude);
        }
    }
}
