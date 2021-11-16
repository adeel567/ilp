package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.awt.geom.Line2D;
import java.util.*;

/**
 * Class for parsing and interacting with the no fly zones.
 */
public class NoFlyZones {

    private static final String SERVER_PATH_TO_NFZ = "buildings/no-fly-zones.geojson";
    private static final int PATHFINDING_ANGLE_INCREMENT = 30;

    private static uk.ac.ed.inf.NoFlyZones instance = null;

    private FeatureCollection NoFlyZones;
    private ArrayList<Polygon> NoFlyZonesPoly;

    /**
     * Uses a singleton pattern, so it has a private constructor.
     */
    private NoFlyZones() {
        this.NoFlyZones = fetchNoFlyZones();
        this.NoFlyZonesPoly = fetchNoFlyZonesPolygons();
    }

    /**
     * Get the instance of NFZ
     * Will be created if it doesn't exist
     * @return NoFlyZones object
     */
    public static NoFlyZones getInstance() {
        if (instance == null) {
            instance = new NoFlyZones();
        }
        return instance;
    }

    /**
     * Connects to server as given in config to retrieve the NoFlyZone
     * @return FeatureCollection of NoFlyZones from JSON
     */
    private FeatureCollection fetchNoFlyZones() {
        String nfzURL = ServerIO.URLFromPath(SERVER_PATH_TO_NFZ);
        String response = ServerIO.getRequest(nfzURL); //get an unparsed response from server

        return FeatureCollection.fromJson(response);
    }

    /**
     * Parses already fetched NoFlyZone into a collection of Polygons.
     * Used for checking intersections.
     * @return a collection (arraylist) of Polygons.
     */
    private ArrayList<Polygon> fetchNoFlyZonesPolygons() {
        assert this.NoFlyZones.features() != null;
        ArrayList<Polygon> polys = new ArrayList<>(); //add all polygons to arraylist
        for (var feature : this.NoFlyZones.features()) {
            polys.add((Polygon) feature.geometry());
        }
        return polys;
    }


    /**
     * Checks if the line formed by two points intersects any member of the NoFlyZone.
     * @param start point of current location
     * @param destination point of desired destination
     * @return true if the move (line formed) would intersect the NoFlyZone
     */
    public boolean doesIntersectNoFly(Point start, Point destination) {

        //build line
        var lineToCheck = new Line2D.Double(start.longitude(), start.latitude(),
                destination.longitude(), destination.latitude());

        for (var x : this.NoFlyZonesPoly) {
            for (int i = 0; i < x.coordinates().get(0).size() - 1; i++) {

                //make a new line from each member
                var polyLine = new Line2D.Double(
                        x.coordinates().get(0).get(i).longitude(),
                        x.coordinates().get(0).get(i).latitude(),
                        x.coordinates().get(0).get(i + 1).longitude(),
                        x.coordinates().get(0).get(i + 1).latitude());

                //determine if the edge of a member intersects the move.
                if (polyLine.intersectsLine(lineToCheck) || lineToCheck.intersectsLine(polyLine)) {
                    return true;
                }
            }
        }
        return false;
    }
}
