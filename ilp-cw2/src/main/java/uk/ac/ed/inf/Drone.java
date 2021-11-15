package uk.ac.ed.inf;

import java.util.ArrayList;

public class Drone {

    private ArrayList<DroneMove> flightPath;

    private final Navigation myNavigation = Navigation.getInstance();
    private LongLat currentLocation;
    private String currentOrderNo;


    public Drone(LongLat initialLocation){
        this.currentLocation = initialLocation;
    }

    public void setCurrentOrder(String orderNo) {
        this.currentOrderNo = orderNo;
    }

    public void flyToStop(Stop stop) {
        var route = myNavigation.getRoute(currentOrderNo,currentLocation,stop.getCoordinates());
        currentLocation = route.get(route.size()-1).getTo();
    }

    public void doHover() {

    }



}
