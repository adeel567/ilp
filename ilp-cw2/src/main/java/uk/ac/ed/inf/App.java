package uk.ac.ed.inf;

/**
 * Run complete Drone Delivery program.
 */
public class App {

    /**
     * Runs the entire program by taking in arguments
     *
     * @param args arguments as specified: day, month, year, web_port, db_port
     */
    public static void main(String[] args) {
        //parse args
        int day = Integer.parseInt(args[0]);
        int month = Integer.parseInt(args[1]);
        int year = Integer.parseInt(args[2]);
        String web_port = args[3];
        String db_port = args[4];

        //setup config with args
        Config config = Config.getInstance();
        config.setDbHost("localhost");
        config.setDbPort(db_port);
        config.setServerHost("localhost");
        config.setServerPort(web_port);

        //get all the orders
        OrderHandler orderHandler = new OrderHandler(day, month, year);
        orderHandler.fetchOrders();

        //build path
        PathBuilder pathBuilder = new PathBuilder(orderHandler);
        pathBuilder.buildGraph();
        pathBuilder.doTour();

        //output information
        FileIO.writeGEOJson(pathBuilder.getFlightPath(), orderHandler.getDate());
        DatabaseIO.writeDeliveriesTable(pathBuilder.getOrdersDelivered());
        DatabaseIO.writeFilepathDatabase(pathBuilder.getFlightPath());


    }
}
