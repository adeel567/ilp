package uk.ac.ed.inf;

/**
 * Represents the edges between orders on TSP graph
 */
public class tspEdge implements Comparable<tspEdge> {

    /** Weight of this edge */
    private final double weight;

    /**
     * Initialise a new edge by setting its weight.
     *
     * @param weight of the edge.
     */
    public tspEdge(double weight) {
        this.weight = weight;
    }

    /**
     * Compare two edges on their weight
     *
     * @param o other edge to compare against
     * @return comparison of the weights
     */
    @Override
    public int compareTo(tspEdge o) { //compare on weight value
        return Double.compare(this.weight, o.weight);
    }
}
