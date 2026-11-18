package Src.structures;

import java.util.*;

public class Graph {
    
    //Store locations and their connected locations
    private Map<String, List<Edge>> adjacencyList;


    //constructor
    public Graph(){
        adjacencyList = new HashMap<>();
    }

    //Inner clss to represent a road connection
    public static class Edge {
        private String destination;
        private double distance;

        public Edge(String destination, double distance){

            this.destination = destination;
            this.distance = distance;
        }

        public String getDestination(){
            return destination;
        }

        public double getDistance(){
            return distance;
        }
        
    }

    // add new location(vertex)
    public void addLocation(String location){
        if (!adjacencyList.containsKey(location)){

            adjacencyList.put(location, new ArrayList<>());

            System.out.println("Location added successfully!");
        }

        else{
            System.out.println("Location already exists.");
        }
    }

    //add a road between two locations
    public void addRoad(String source, String destination, double distance){

        if(distance <=0){
            System.out.println("Distance must be greater than zero");
            return;
        }

        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)){

            System.out.println("One or both location do not exists.");
            return;
        }

        if(hasRoad(source, destination)){
            
            System.out.println("Road already exists.");
            return;
        }

        //add connection in both directions
        adjacencyList.get(source).add(new Edge(destination, distance));

        adjacencyList.get(destination).add(new Edge(source, distance));

        System.out.println("Road added successfully!");
    }

    //check whether a road already exists
    public boolean hasRoad(String source, String destination){

        if(!adjacencyList.containsKey(source)){
            return false;
        }

        for (Edge edge : adjacencyList.get(source)){

            if(edge.getDestination().equalsIgnoreCase(destination)){
                return true;
            }
        }

        return false;
    }

    //display all locations and road connections
    public void displayGraph(){

        if(adjacencyList.isEmpty()){
            System.out.println("No locations available.");
            return;
        }

        System.out.println("\n==================== ROAD NETWORK ====================");

        for(String locaction : adjacencyList.keySet()){

            System.out.println("\nLocation: " + locaction);

            for(Edge edge : adjacencyList.get(locaction)){
                System.out.println(" -> " + edge.getDestination() + " (" + edge.getDistance() + " km");
            }
        }

        System.out.println("=========================================================");
    }

    //get all locations
    public Set<String> getLocations(){
        return new HashSet<>(adjacencyList.keySet());
    }

    //get connected roads for a location
    public List<Edge> getNeighbours(String location){

        if(!adjacencyList.containsKey(location)){
            return new ArrayList<>();
        }

        return new ArrayList<>(adjacencyList.get(location));
    }

    //check whether a location exists
    public boolean containsLocation(String location){
        return adjacencyList.containsKey(location);
    }

    //get total number of locations
    public int getLocationCount(){
        return adjacencyList.size();
    }


}
