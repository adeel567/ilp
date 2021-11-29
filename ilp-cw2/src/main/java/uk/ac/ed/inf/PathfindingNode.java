package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * An expansion of LongLat for use in the A* pathfinding.
 */
public class PathfindingNode extends LongLat implements Comparable<PathfindingNode> {
    private double f = Double.MAX_VALUE;
    private double g = Double.MAX_VALUE;
    private int angle;
    private PathfindingNode parent;

    /**
     * Create using longitude and latitude, like its parent, LongLat.
     * @param longitude of location
     * @param latitude of location.
     */
    public PathfindingNode(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Generate all possible neighbours that could possibly be flown to.
     * Restrict by setting the increment of angle.
     * @param inc of how far the next neighbour should be in degrees.
     * @return a collection of neighbours.
     */
    public ArrayList<PathfindingNode> generateNeighbours(int inc) {
        ArrayList<PathfindingNode> neighbours = new ArrayList<>();
        for (int i = LongLat.MIN_ANGLE; i<= LongLat.MAX_ANGLE; i+=inc) {
            var x = this.nextPosition(i);
            x.setAngle(i);
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
        return new LongLat(this.getLongitude(), this.getLatitude());
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

        final PathfindingNode other = (PathfindingNode) obj;
        return this.getLongitude() == other.getLongitude() && this.getLatitude() == other.getLatitude()
                && this.getF() == other.getF() && this.getG() == other.getG();
    }

    /**
     * Comparison of nodes are done on their f weight.
     * @param o node to compare against.
     * @return comparison of the f weights.
     */
    @Override
    public int compareTo(PathfindingNode o) {
        return Double.compare(this.getF(), o.getF());    }

    /**
     * Generate a new node for new location after a move by angle.
     * @param angle if the angle is from 0 to 350 and a multiple of 10 then the drone moves.
     * @return node of new location.
     */
    @Override
    public PathfindingNode nextPosition(int angle) {
        LongLat x = super.nextPosition(angle);
        return new PathfindingNode(x.getLongitude(), x.getLatitude());
    }

    /**
     * Override toString by appending toString of super with f and g weights.
     * @return String representation of a node.
     */
    @Override
    public String toString(){
        return super.toString() + " f: " + getF() + " g: " + getG();
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public PathfindingNode getParent() {
        return parent;
    }

    public void setParent(PathfindingNode parent) {
        this.parent = parent;
    }
}
