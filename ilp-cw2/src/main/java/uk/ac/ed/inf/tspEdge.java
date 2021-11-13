package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class tspEdge implements Comparable<tspEdge> {

    private double weight;
    private List<DroneMove> route;

    public tspEdge(double weight, ArrayList<DroneMove> route) {
        this.weight = weight;
        this.route = route;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<DroneMove> getRoute() {
        return route;
    }

    public void setRoute(List<DroneMove> route) {
        this.route = route;
    }


    @Override
    public int compareTo(tspEdge o) { //compare on weight value
        return Double.compare(this.weight, o.weight);
    }
}
