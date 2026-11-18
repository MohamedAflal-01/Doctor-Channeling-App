package Src.algorithms;

import Src.structures.Graph;
import java.util.*;

public class FloydWarshall {
    
    //set attributes
    private Graph graph;
    private double[][] distances;
    private String[] locations;
    private Map<String, Integer> locationIndexes;

    //call the constructor
    public FloydWarshall(Graph graph){
        this.graph = graph;
        initializeGraph();
    }

    //initialize distance matrix
    private void initializeGraph(){

        Set<String> locationSet = graph.getLocations();

        locations = locationSet.toArray(new String[0]);

        int size = locations.length;

        distances = new double[size][size];

        locationIndexes = new HashMap<>();

        //assign index to each location
        for(int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                if(i==j){
                    distances[i][j] = 0;
                }
                else{
                    distances[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        //add road distance
        for(int i=0; i<size; i++){
            String location = locations[i];

            for(Graph.Edge edge : graph.getNeighbours(location)){

                int destinationIndex = locationIndexes.get(edge.getDestination());

                distances[i][destinationIndex] = edge.getDistance();
            }
        }
    }

    //execute floyd-warshall algorithm
    public void calculateShortestPaths(){
        int size = locations.length;

        for(int k=0; k<size; k++){
            for(int i=0; i<size; i++){
                for(int j=0; j<size; j++){
                    if (distances[i][k] != Double.POSITIVE_INFINITY && distances[k][j] !=Double.POSITIVE_INFINITY){

                        double newDistance = distances[i][k] + distances[k][j];

                        if(newDistance <distances[i][j]){
                            distances[i][j] = newDistance;
                        }
                    }
                }
            }
        }

        System.out.println("Shortest paths calculated successfully!");
    }

    //get shortest distance between two locations
    public double getShortestDistance(String source, String destination){

        Integer sourceIndex = locationIndexes.get(source);

        Integer destinationIndex = locationIndexes.get(destination);

        if (sourceIndex == null || destinationIndex == null){

            System.out.println("Invalid location.");
            return Double.POSITIVE_INFINITY;
        }

        return distances[sourceIndex][destinationIndex];
    }

    //display all shortest distances
    public void displayAllShortDistances(){

        System.out.println("\n========== SHORTEST DISTANCE MATRIX ==========");

        System.out.printf("%-18s", "Location");

        for(String location : locations){
            System.out.printf("%-18s", location);
        }

        System.out.println();

        for (int i=0; i<locations.length; i++){
            System.out.printf("%-18s", locations[i]);

            for(int j=0; j<locations.length; j++){
                if(distances[i][j] == Double.POSITIVE_INFINITY){

                    System.out.printf("%-18s", "INF");
                }
                else{

                    System.out.printf("%-18.2f", distances[i][j]);
                }
            }
            System.out.println();
        }
        
        System.out.println("==============================================");

    }

    //display shortest route distance
    public void displayShortestDistance(String source, String destination){

        double distance = getShortestDistance(source, destination);

        if(distance == Double.POSITIVE_INFINITY){
            System.out.println("No route available");
        }

        else{
            System.out.println("Shortest distance from " + source + "to " + destination + " is " + distance + " km");
        }
    }
}
