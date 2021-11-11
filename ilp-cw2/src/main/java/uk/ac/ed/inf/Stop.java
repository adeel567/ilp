package uk.ac.ed.inf;

public class Stop {
    public LongLat coordinates;
    public String id;
    public String orderNo;

    public Stop(String id, LongLat coordinates, String orderNo) {
        this.id = id;
        this.coordinates = coordinates;
        this.orderNo = orderNo;
    }

    @Override
    public String toString(){
        return "id: " + id + " coors: " + coordinates + " orderNo: " +  orderNo;
    }

}
