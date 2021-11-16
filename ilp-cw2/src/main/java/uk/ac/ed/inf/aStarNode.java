package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * An expansion of LongLat for use in the A* pathfinding.
 */
public class aStarNode extends LongLat implements Comparable<aStarNode> {
    protected double f = Double.MAX_VALUE;
    protected double g = Double.MAX_VALUE;
    protected int angle;
    protected aStarNode parent;

    /**
     * Create using longitude and latitude, like its parent, LongLat.
     * @param longitude of location
     * @param latitude of location.
     */
    public aStarNode(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Generate all possible neighbours that could possibly be flown to.
     * Restrict by setting the increment of angle.
     * @param inc of how far the next neighbour should be in degrees.
     * @return a collection of neighbours.
     */
    public ArrayList<aStarNode> generateNeighbours(int inc) {
        ArrayList<aStarNode> neighbours = new ArrayList<>();
        for (int i = LongLat.MIN_ANGLE; i<= LongLat.MAX_ANGLE; i+=inc) {
            var x = this.nextPosition(i);
            x.angle = i;
            neighbours.add(x);
        }
        return neighbours;
    }

    /**
     * Generate a LongLat from this node.
     * Could also be done with casting.
     * @return a LongLat of the location of node.
     */
    public LongLat asLongLat() {
        return new LongLat(this.longitude, this.latitude);

    }


    /**
     * Two nodes are equal when they have same location, f, and g.
     * @param obj LongLat to compare against
     * @return boolean of whether they are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final aStarNode other = (aStarNode) obj;
        return this.longitude == other.longitude && this.latitude == other.latitude
                && this.f == other.f && this.g == other.g;
    }

    /**
     * Comparison of nodes are done on their f weight.
     * @param o node to compare against.
     * @return comparison of the f weights.
     */
    @Override
    public int compareTo(aStarNode o) {
        return Double.compare(this.f, o.f);    }

    /**
     * Generate a new node for new location after a move by angle.
     * @param angle if the angle is from 0 to 350 and a multiple of 10 then the drone moves.
     * @return node of new location.
     */
    @Override
    public aStarNode nextPosition(int angle) {
        LongLat x = super.nextPosition(angle);
        return new aStarNode(x.longitude, x.latitude);
    }

    @Override
    public String toString(){
        return super.toString() + " f: " + f + " g: " +g;
    }
}
