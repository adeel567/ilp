package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for acting on the various menus.
 */
public class Menus {

    /** Constant for the standard delivery charge */
    private static final int STANDARD_DELIVERY_CHARGE = 50;

    /** Constant for the path on the server where the menu is located */
    private static final String SERVER_PATH_TO_MENU = "menus/menus.json";

    /** Singleton pointer */
    private static Menus instance = null;

    /** Shops obtained from server */
    private final ArrayList<Shop> shops;

    /** Parsed HashMap linking items to their prices */
    private final HashMap<String, Integer> itemPrices;

    /** Parsed HashMap linking items to their shops */
    private final HashMap<String, Shop> itemStops;

    /**
     * Uses a singleton pattern, so it has a private constructor.
     */
    private Menus() {
        this.shops = new ArrayList<>(getShopDetails()); //get all shops once.
        this.itemPrices = new HashMap<>(getItemPrices());
        this.itemStops = new HashMap<>(getItemStops());
    }

    /**
     * Get the instance of Menus
     * Will be created if it doesn't exist
     *
     * @return Menus object
     */
    public static Menus getInstance() {
        if (instance == null) {
            instance = new Menus();
        }
        return instance;
    }

    /**
     * Calculates the total cost including the delivery cost of ordering various items.
     *
     * @param items takes multiple strings of item names to calculate cost of ordering them.
     * @return the total cost of delivery in pence.
     */
    public int getDeliveryCost(String... items) {
        int totalPrice = STANDARD_DELIVERY_CHARGE; //all orders must start from base price of delivery
        for (String itemName : items) {
            totalPrice += itemPrices.get(itemName);
        }
        return totalPrice;
    }

    /**
     * Gets the set of shops that need to be visited for a delivery.
     *
     * @param items which have been ordered
     * @return the set of shops for these items as ArrayList (to use .get()).
     */
    public ArrayList<Shop> getDeliveryShops(String... items) {
        Set<Shop> stops = new HashSet<>();
        for (String itemName : items) {
            stops.add(itemStops.get(itemName));
        }

        return new ArrayList<>(stops);

    }

    /**
     * Obtain the pairs of items and prices from all shops.
     *
     * @return a HashMap of item and price pairs.
     */
    private HashMap<String, Integer> getItemPrices() {
        HashMap<String, Integer> itemPrices = new HashMap<>(); //allows for O(1) lookups

        for (Shop shop : this.shops) {
            for (Shop.Menu menu : shop.menu) {
                itemPrices.put(menu.item, menu.pence); //store all values once
            }
        }
        return itemPrices;
    }

    /**
     * Obtains and parses all of the shops and their details from the server.
     *
     * @return a collection of parsed Shop objects as ArrayList.
     */
    private ArrayList<Shop> getShopDetails() {
        String MenuURL = ServerIO.URLFromPath(SERVER_PATH_TO_MENU);
        String response = ServerIO.getRequest(MenuURL); //get an unparsed response from server

        ArrayList<Shop> parsedShops;
        Type listType = new TypeToken<ArrayList<Shop>>() {
        }.getType(); //GSON needs to be told type
        parsedShops = new Gson().fromJson(response, listType);
        return parsedShops;
    }

    /**
     * Obtain the pairs of items and respective shops
     *
     * @return a HashMap of the item and shop pairs.
     */
    private HashMap<String, Shop> getItemStops() {
        HashMap<String, Shop> itemStops = new HashMap<>();
        for (Shop shop : this.shops) {
            for (Shop.Menu menu : shop.menu) {
                itemStops.put(menu.item, shop); //store all values once
            }
        }
        return itemStops;
    }
}
