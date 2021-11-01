package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.List;

public class DroneMove {
    private LongLat from;
    private LongLat to;
    private int angle;
    private List<Point> points;

    public DroneMove(LongLat from, LongLat to, int angle) {
        this.from = from;
        this.to = to;
        this.angle = angle;
    }

    public LongLat getFrom() {
        return from;
    }

    public void setFrom(LongLat from) {
        this.from = from;
    }

    public LongLat getTo() {
        return to;
    }

    public void setTo(LongLat to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return (String.format("From: %s, to: %s, angle: %s", this.from,this.to,this.angle));
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        assert angle%10 == 0 : "invalid angle";
        this.angle = angle;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
