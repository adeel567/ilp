package uk.ac.ed.inf;

/**
 * Represents the edges between orders on TSP graph
 */
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

    /**
     * Compare two edges on their weight
     * @param o other edge to compare against
     * @return comparison of the weights
     */
    @Override
    public int compareTo(tspEdge o) { //compare on weight value
        return Double.compare(this.weight, o.weight);
    }
}
