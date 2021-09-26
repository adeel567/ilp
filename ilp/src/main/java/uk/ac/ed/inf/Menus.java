package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Menus {

    private final int STANDARD_DELIVERY_CHARGE = 50;

    private String machineName;
    private String serverPort;

    public Menus(String machineName, String serverPort) {
        this.machineName = machineName;
        this.serverPort = serverPort;
    }

    public int getDeliveryCost(String... items){
        URL url = null;
        try {
            url = new URL(String.format("http://%s:%s/menus/menus.json",this.machineName,this.serverPort));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<AllMenus> parsedMenus;
        Type listType = new TypeToken<List<AllMenus>>() {}.getType();
        parsedMenus = new Gson().fromJson(reader, listType);
        HashMap<String,Integer> itemPrices = new HashMap<>();

        for (AllMenus store: parsedMenus) {
            for (menu menu: store.menu) {
                itemPrices.put(menu.item,menu.pence);
            }
        }

        int totalPrice = STANDARD_DELIVERY_CHARGE;
        for (String itemName : items) {
            totalPrice += itemPrices.get(itemName);
        }
        return totalPrice;
    }
}
