package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.List;

public class Stop {
    public LongLat coordinates;
 //   public double distanceTo;
    public String id;
//    public List<DroneMove> routeTo;

    public Stop(String id, LongLat coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }
}
