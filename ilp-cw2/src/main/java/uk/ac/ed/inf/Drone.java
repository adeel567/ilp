package uk.ac.ed.inf;

import java.util.ArrayList;

public class Drone {
    public static final int MOVES_ALLOWED = 1500;
    private ArrayList<DroneMove> flightpath;

    public Drone(){
        this.flightpath = new ArrayList<>();
    }

    public void addMoves(ArrayList<DroneMove> dms) {
        flightpath.addAll(dms);
    }

    public void addMove(DroneMove dm) {
        flightpath.add(dm);
    }

    public void doHover(){
        var latest = flightpath.get(flightpath.size()-1);
        flightpath.add(new DroneMove(latest.getId(),latest.getTo(),latest.getTo(),LongLat.JUNK_ANGLE));
    }

    public void doMove() {}
}
