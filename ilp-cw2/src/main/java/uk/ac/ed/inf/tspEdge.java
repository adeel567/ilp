package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class tspEdge implements Comparable<tspEdge> {

    private double weight;
    private List<DroneMove> route;
    private int moves;

    public tspEdge(double weight, ArrayList<DroneMove> route, int moves) {
        this.weight = weight;
        this.route = route;
        this.moves = moves;
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

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    @Override
    public int compareTo(tspEdge o) { //compare on weight value
        return Double.compare(this.weight, o.weight);
    }
}
