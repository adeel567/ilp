package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Menus {

    //constant for the  delivery charge
    private static final int STANDARD_DELIVERY_CHARGE = 50;

    //constants for using server and to ensure there is only one HttpClient
    private static final String SERVER_PATH_TO_MENU = "menus/menus.json";
    private static final int TIMEOUT_DURATION_IN_SECS = 10;
    private static final int HTTP_SUCCESS = 200;
    private static final HttpClient client = HttpClient.newHttpClient();

    private final String serverName;
    private final String serverPort;

    public Menus(String serverName, String serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    /**
     * Calculates the total cost including the delivery cost of ordering various items.
     * @param items takes multiple strings of item names to calculate cost of ordering.
     * @return the total cost of delivery in pence.
     */
    public int getDeliveryCost(String... items){
        String MenuURL = String.format("http://%s:%s/%s",this.serverName,this.serverPort, SERVER_PATH_TO_MENU);
        String response = getJSON(MenuURL);

        List<RestaurantDetails> restaurants = parseRestaurantDetails(response);

        HashMap<String,Integer> itemPrices = new HashMap<>(); //allows for O(1) lookups

        for (RestaurantDetails restaurant: restaurants) {
            for (RestaurantDetails.Menu menu: restaurant.menu) {
                itemPrices.put(menu.item,menu.pence); //store all values once
            }
        }

        int totalPrice = STANDARD_DELIVERY_CHARGE; //all orders must start from base price of delivery
        for (String itemName : items) {
            totalPrice += itemPrices.get(itemName);
        }
        return totalPrice;
    }

    private String getJSON(String urlString) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.of(TIMEOUT_DURATION_IN_SECS,SECONDS)) //timeout in case server overloaded
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HTTP_SUCCESS) {
                throw new RuntimeException(String.format("JSON retrieval failed with status code: %s",
                        response.statusCode()));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(String.format("Unable to connect to server %s on port %s",
                    this.serverName, this.serverPort));
        }
        return response.body();
    }

    private List<RestaurantDetails> parseRestaurantDetails(String toParse) {
        List<RestaurantDetails> parsedRestaurants;
        Type listType = new TypeToken<List<RestaurantDetails>>() {}.getType(); //GSON needs type of the list of objects
        parsedRestaurants = new Gson().fromJson(toParse, listType);
        return parsedRestaurants;
    }
}
