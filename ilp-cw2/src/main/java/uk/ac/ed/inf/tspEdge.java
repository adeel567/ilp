package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class tspEdge implements Comparable<tspEdge> {

    public double weight;
    public List<DroneMove> route;
    public int moves;
    public int distXY;
    public int distAll;

    public tspEdge(double weight, ArrayList<DroneMove> route, int moves, int distXY, int distAll) {
        this.weight = weight;
        this.route = route;
        this.moves = moves;
        this.distXY = distXY;
        this.distAll = distAll;
    }

    public tspEdge() {

    }

    @Override
    public int compareTo(tspEdge o) {
        return Double.compare(this.weight, o.weight);
    }
}
