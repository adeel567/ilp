package uk.ac.ed.inf;

public class Stop {
    public LongLat coordinates;
    public String id;

    public Stop(String id, LongLat coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }
}
