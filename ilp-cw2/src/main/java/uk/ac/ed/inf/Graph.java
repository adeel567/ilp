package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.*;

public class Graph {

    private int numOfNodes;

    private double[][] weightMatrix;

    private List<Point>[][] routeMatrix;

    private int[][] moveCountMatrix;

    private boolean[] visited;


    public Graph(int numOfNodes) {
        this.weightMatrix = new double[numOfNodes][numOfNodes];

//        for (int i=0; i<numOfNodes; i++) {
//            this.weightMatrix.add((new ArrayList<>()));
//        }

        this.visited = new boolean[numOfNodes];
        this.moveCountMatrix = new int[numOfNodes][numOfNodes];
        this.routeMatrix = new List[numOfNodes][numOfNodes];
    }

    public void initEdges(int source, int destination, double weight) {
        weightMatrix[source][destination] = weight;
    }

    public void setVisited(int index) {
        this.visited[index] = true;
    }

    public double getWeight(int source, int destination) {
        return this.weightMatrix[source][destination];
    }

    public void addMoves(int source, int destination, int moves) {
        this.moveCountMatrix[source][destination] = moves;
    }

    public void addRoutes(int source, int destination, List<Point> points) {
        this.routeMatrix[source][destination] = points;
    }

    public double[] getEdges(int source) {
        return this.weightMatrix[source];
    }
}
