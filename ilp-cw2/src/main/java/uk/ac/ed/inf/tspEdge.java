package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class tspEdge implements Comparable<tspEdge> {

    private double weight;

    public tspEdge(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(tspEdge o) { //compare on weight value
        return Double.compare(this.weight, o.weight);
    }
}
