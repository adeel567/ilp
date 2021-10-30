package uk.ac.ed.inf;

import java.util.ArrayList;

public class aStarNode extends LongLat implements Comparable<aStarNode> {

    /**
     * Create a LongLat object by passing values for longitude and latitude respectively.
     *
     * @param longitude a double for longitude
     * @param latitude  a double for latitude
     */

    public double f = 0;
    public double g = 0;
    public int angle;
    public aStarNode parent;

    public aStarNode(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public ArrayList<aStarNode> generateNeighbours(int inc) {
        ArrayList<aStarNode> peepee = new ArrayList<>();
        for (int i =0;i<=350;i+=inc) {
            var x = this.nextPosition(i);
            x.angle = i;
            peepee.add(x);
        }
        return peepee;
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
