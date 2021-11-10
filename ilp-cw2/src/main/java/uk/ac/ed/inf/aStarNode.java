package uk.ac.ed.inf;

import java.util.ArrayList;

public class aStarNode extends LongLat implements Comparable<aStarNode> {
    public double f = Double.MAX_VALUE;
    public double g = Double.MAX_VALUE;
    public int angle;
    public aStarNode parent;

    public aStarNode(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public ArrayList<aStarNode> generateNeighbours(int inc) {
        ArrayList<aStarNode> neighbours = new ArrayList<>();
        for (int i = LongLat.MIN_ANGLE; i<= LongLat.MAX_ANGLE; i+=inc) {
            var x = this.nextPosition(i);
            x.angle = i;
            neighbours.add(x);
        }
        return neighbours;
    }

    public LongLat asLongLat() {
        return new LongLat(this.longitude, this.latitude);
    }

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

    @Override
    public int compareTo(aStarNode o) {
        return Double.compare(this.f, o.f);    }

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
