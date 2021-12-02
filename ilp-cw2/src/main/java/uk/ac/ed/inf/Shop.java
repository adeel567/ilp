package uk.ac.ed.inf;

/**
 * Class for structuring the details of a shop.
 * Required for parsing JSON.
 */
public class Shop {
    String name;
    String location;

    Menu[] menu;

    public static class Menu {
        String item;
        int pence;
    }
}
