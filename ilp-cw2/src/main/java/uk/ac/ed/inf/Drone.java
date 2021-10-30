package uk.ac.ed.inf;

import java.util.ArrayList;

public class Drone {

    public int moves = 1500;
    public ArrayList<DroneMove> flightpath;
    private static final double STRAIGHT_LINE_DISTANCE = 0.00015;

    public Drone() {
    }

    public void doMove(LongLat from, LongLat to) {
        assert (from.distanceTo(to) != STRAIGHT_LINE_DISTANCE) : "move is invalid in distance";
    }
}
