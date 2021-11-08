package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;

public class DroneMove {
    private LongLat from;
    private LongLat to;
    private int angle;
    private String id;

    public DroneMove(String id, LongLat from, LongLat to, int angle) {
        this.id = id;
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
        return (String.format("Job: %s, from: %s, to: %s, angle: %s", this.id, this.from,this.to,this.angle));
    }

    private static ArrayList<Point> movesToPath(ArrayList<DroneMove> dms) {
        var lls = new ArrayList<Point>();
        lls.add(dms.get(0).getFrom().toPoint());
        lls.add(dms.get(0).getTo().toPoint());

        if (dms.size() > 1) {
            for (int i = 1, dmsSize = dms.size(); i < dmsSize; i++) {
                DroneMove dm = dms.get(i);
                lls.add(dm.getTo().toPoint());
            }
        }
        System.out.println("points " + lls.size());
        return lls;
    }


    private static FeatureCollection getRouteAsFC(List<Point> path) {
        var feature = Feature.fromGeometry(
                (Geometry) LineString.fromLngLats(path));

        var fc = FeatureCollection.fromFeature(feature);
        System.out.println(fc.toJson());
        return fc;
    }

    public static FeatureCollection getMovesAsFC(ArrayList<DroneMove> dms) {
        var x = movesToPath(dms);
        return getRouteAsFC(x);
    }
}
