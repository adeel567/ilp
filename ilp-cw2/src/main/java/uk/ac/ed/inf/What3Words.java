package uk.ac.ed.inf;

import com.google.gson.Gson;

/**
 * Class for parsing What3Words from a server.
 */
public class What3Words {
    private LongLat coordinates;
    private String words;

    private static class w3wCoordinates { //parsing JSON
        coordinates coordinates;
        private static class coordinates {
            double lng;
            double lat;

        }
    }

    /**
     * Created by passing the What3Words address.
     * Constructor parses information from server.
     * @param w3wString address of location.
     */
    public What3Words(String w3wString) {
        this.words = w3wString;

        String[] words = w3wString.split("\\.");
        String w3wURL = ServerIO.URLFromPath(String.format("words/%s/%s/%s/details.json",words[0],words[1],words[2]));
        String response = ServerIO.getRequest(w3wURL); //get an unparsed response from server
        w3wCoordinates test = new Gson().fromJson(response,What3Words.w3wCoordinates.class);
        this.coordinates = new LongLat(test.coordinates.lng,test.coordinates.lat);
    }

    /**
     * What3Words as a LongLat.
     * @return
     */
    public LongLat getCoordinates() {
        return this.coordinates;
    }
}
