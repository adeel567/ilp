package uk.ac.ed.inf;

/**
 * Class for structuring the details of a shop.
 * Required for parsing JSON.
 */
public class Shop {
    /** Name of Shop */
    private String name;

    /** Location of shop as What3Words */
    private String location;

    /** Array of all the items on the shop's menu */
    private Menu[] menu;

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public Menu[] getMenu() {
        return menu;
    }

    /**
     * Parsing the menu items for each parsed shop from JSON.
     */
    public static class Menu {
        /** Name of item */
        private String item;

        /** Price in pence of item */
        private int pence;

        public String getItem() {
            return item;
        }

        public int getPence() {
            return pence;
        }
    }
}
