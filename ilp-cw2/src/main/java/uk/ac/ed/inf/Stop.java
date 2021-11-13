package uk.ac.ed.inf;

/**
 * Class for representing all possible stops a drone can make
 * Stores the location and identification information.
 */
public class Stop {
    private LongLat coordinates;
    private String id;
    private String orderNo;

    public Stop(String id, LongLat coordinates, String orderNo) {
        this.id = id;
        this.coordinates = coordinates;
        this.orderNo = orderNo;
    }

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

    public LongLat getStart() {
        return coordinates;
    }

    public LongLat getDestination() {
        return coordinates;
    }

}
