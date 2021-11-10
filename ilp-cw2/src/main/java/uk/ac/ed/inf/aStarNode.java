package uk.ac.ed.inf;

import java.util.ArrayList;

public class aStarNode extends LongLat implements Comparable<aStarNode> {
    public double f;
    public double g;
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
    public int compareTo(aStarNode o) {
        return Double.compare(this.f, o.f);    }

    @Override
    public aStarNode nextPosition(int angle) {
        LongLat x = super.nextPosition(angle);
        return new aStarNode(x.longitude, x.latitude);
    }
}
