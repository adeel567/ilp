package uk.ac.ed.inf;

import java.util.List;

/**
 * Interface for Pathfinding, this is so that a variety of search algorithms can be tried and implemented to
 * determine what is best.
 */
public interface PathfindingInterface {
    /**
     * Computes the best route from one LongLat to another.
     * Implement with best algorithm of your choosing.
     * @param startLL location to start at
     * @param endLL   location to end 'close-to'
     * @return a collection of pathfinding nodes.
     */
    List<PathfindingNode> routeTo(LongLat startLL, LongLat endLL) ;
}
