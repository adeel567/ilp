package uk.ac.ed.inf;

public class RestaurantDetails {
    String name;
    String location;

    Menu[] menu;
    public static class Menu {
        String item;
        int pence;
    }
}
