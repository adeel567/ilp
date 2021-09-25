package uk.ac.ed.inf;

public class Menus {

    private final int STANDARD_DELIVERY_CHARGE = 50;

    private String machineName;
    private String serverPort;

    public Menus(String machineName, String serverPort) {
        this.machineName = machineName;
        this.serverPort = serverPort;
    }

    public int getDeliveryCost(String... items) {
        return 50;
    }
}
