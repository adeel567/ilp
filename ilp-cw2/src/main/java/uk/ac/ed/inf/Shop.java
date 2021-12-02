package uk.ac.ed.inf;

/**
 * Class for structuring the details of a shop.
 * Required for parsing JSON. So cannot attributes are not 'final'.
 */
public class Shop {
    String name;
    String location;

    Menu[] menu;

    /**
     * Parsing the menu items for each parsed shop from JSON.
     */
    public static class Menu {
        String item;
        int pence;
    }
}
