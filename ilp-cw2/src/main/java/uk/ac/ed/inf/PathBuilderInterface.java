package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Interface for PathBuilding, in case a different algorithm is to be used in the future instead of a Greedy-like
 * algorithm.
 */
public interface PathBuilderInterface {
    /**
     * Build a graph containing the Orders to build a route from.
     */
    void buildGraph();

    /**
     * From graph that has been built, determine the best route.
     */
    void doTour();

    /**
     * Get which Orders will be delivered today.
     * @return all the Order objects that will be delivered.
     */
    ArrayList<Order> getOrdersDelivered();

    ArrayList<DroneMove> getFlightPath();

    int getProfit();

    int getProfitLost();

    double getMonetaryValue();
}
