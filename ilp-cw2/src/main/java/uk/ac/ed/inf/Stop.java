package uk.ac.ed.inf;

/**
 * Class for representing all possible stops a drone can make
 * Stores the location and identification information.
 */
public class Stop {

    private final LongLat coordinates;
    private final String id;
    private final String orderNo;

    /**
     * Create a new stop from its associated order, location and stop name
     * @param id a friendly identifier for the stop, such as a shop name
     * @param coordinates the coordinates of the stop, in LongLat format
     * @param orderNo the order associated with this stop
     */
    public Stop(String id, LongLat coordinates, String orderNo) {
        this.id = id;
        this.coordinates = coordinates;
        this.orderNo = orderNo;
    }

    /**
     * Override to string to output what is needed for debugging.
     * @return String value of a Stop.
     */
    @Override
    public String toString(){
        return "id: " + id + " coors: " + coordinates + " orderNo: " +  orderNo;
    }

    public LongLat getCoordinates() {
        return coordinates;
    }

    public String getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

}
