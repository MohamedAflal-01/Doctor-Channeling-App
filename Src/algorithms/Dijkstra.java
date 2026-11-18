package Src.algorithms;

import Src.structures.Graph;
import java.util.*;

public class Dijkstra {
    
    //Store the graph
    private Graph graph;

    //create a constructor
    public Dijkstra(Graph graph){
        this.graph = graph;
    }

    //find the shortest distance from a starting location
    public Map<String, Double> findShortestDistances(String startLocation){

        Map<String, Double> distances = new HashMap<>();

        //priority queue to process nearest location first
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        //Initialize all locations with infinity
        for(String location : graph.getLocations()){

            distances.put(location, Double.POSITIVE_INFINITY);
        }

        //check starting location
        if(!graph.containsLocation(startLocation)){

            System.out.println("Starting location not found. Please try again!");
            return distances;
        }

        distances.put(startLocation, 0.0);
        priorityQueue.add(startLocation);

        //process location
        while(!priorityQueue.isEmpty()){

            String currentLocation = priorityQueue.poll();

            double currentDistance = distances.get(currentLocation);

            //check neighbouring locations
            for(Graph.Edge edge : graph.getNeighbours(currentLocation)){

                String neighbour = edge.getDestination();

                double newDistance = currentDistance + edge.getDistance();

                //update shorter distance
                if (newDistance < distances.get(neighbour)){

                    distances.put(neighbour, newDistance);

                    priorityQueue.add(neighbour);
                }
            }
        }

        return distances;
    }

    //find shortest route between two location
    public List<String> findShortestRoute(String startLocation, String destination){

        Map<String, Double> distances = new HashMap<>();

        Map<String, String> previousLocations = new HashMap<>();

        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        //initialize distances
        for(String location : graph.getLocations()){
            distances.put(location, Double.POSITIVE_INFINITY);
        }

        if (!graph.containsLocation(startLocation) || !graph.containsLocation(destination)){

            System.out.println("Invalid location");
            return new ArrayList<>();
        }

        distances.put(startLocation, 0.0);
        priorityQueue.add(startLocation);

        while(!priorityQueue.isEmpty()){

            String currentLocation = priorityQueue.poll();

            if(currentLocation.equals(destination)){
                break;
            }

            for(Graph.Edge edge : graph.getNeighbours(currentLocation)){

                String neighbour = edge.getDestination();

                double newDistance = distances.get(currentLocation) + edge.getDistance();

                if (newDistance < distances.get(neighbour)){

                    distances.put(neighbour, newDistance);

                    previousLocations.put(neighbour, currentLocation);

                    priorityQueue.add(neighbour);
                }
            }
        }

        //reconstruct shortest route 
        List<String> route = new ArrayList<>();

        if (distances.get(destination) == Double.POSITIVE_INFINITY){
            System.out.println("No route available.");
            return route;
        }

        String current = destination;

        while(current != null){
            route.add(current);

            current = previousLocations.get(current);
        }

        Collections.reverse(route);

        return route;
    }

    //get shortest distance between two location
    public double getShortestDistance(String startLocation, String destination){

        Map<String, Double> distances = findShortestDistances(startLocation);

        if(!distances.containsKey(startLocation)){
            return Double.POSITIVE_INFINITY;
        }

        return distances.get(destination);
    }

    public static void main(String[] args) {

        Graph graph = new Graph();

        // Add locations
        graph.addLocation("Kalmunai");
        graph.addLocation("Maruthamunai");
        graph.addLocation("Sainthamaruthu");
        graph.addLocation("Addalaichenai");

        // Add roads
        graph.addRoad("Kalmunai", "Maruthamunai", 5);
        graph.addRoad("Kalmunai", "Sainthamaruthu", 3);
        graph.addRoad("Maruthamunai", "Addalaichenai", 12);
        graph.addRoad("Sainthamaruthu", "Addalaichenai", 8);

        // Create Dijkstra object
        Dijkstra dijkstra = new Dijkstra(graph);

        // Calculate shortest route
        System.out.println("\nShortest Route:");

        System.out.println(
            dijkstra.findShortestRoute(
                "Kalmunai",
                "Addalaichenai"
            )
        );

        // Calculate shortest distance
        System.out.println("\nShortest Distance:");

        System.out.println(
            dijkstra.getShortestDistance(
                "Kalmunai",
                "Addalaichenai"
            ) + " km"
        );
    }

}
