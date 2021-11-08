package uk.ac.ed.inf;

import com.google.gson.Gson;

public class What3Words {
    public LongLat coordinates;
    public String words;
    private Config config = Config.getInstance();

    private static class w3wCoordinates { //parsing JSON
        coordinates coordinates;
        private static class coordinates {
            double lng;
            double lat;

        }
    }


    public What3Words(String w3wString) {
        this.words = w3wString;

        String[] words = w3wString.split("\\.");
        String w3wURL = String.format("http://%s:%s/words/%s/%s/%s/details.json",config.getServerHost(),
                config.getServerPort(), words[0],words[1],words[2]);
        String response = ServerIO.getRequest(w3wURL); //get an unparsed response from server
        w3wCoordinates test = new Gson().fromJson(response,What3Words.w3wCoordinates.class);
        this.coordinates = new LongLat(test.coordinates.lng,test.coordinates.lat);
    }

    public LongLat getCoordinates() {
        return this.coordinates;
    }
}
