package Src.services;

import Src.structures.Graph;

import java.util.List;

import Src.algorithms.Dijkstra;
import Src.algorithms.FloydWarshall;

public class HomeVisitService {
    
    //declare attributes
    private Graph graph;
    private Dijkstra dijkstra;
    private FloydWarshall floydWarshall;

    //set constructor
    public HomeVisitService(){

        graph = new Graph();

        dijkstra = new Dijkstra(graph);

        floydWarshall = new FloydWarshall(graph);
    }

    //add a new location
    public void addLocation(String location){

        graph.addLocation(location);
    }

    //add a road between two location
    public void addRoad(String source, String destination, double distance){

        graph.addRoad(source, destination, distance);
    }

    //display all location and roads
    public void displayLocations(){
        graph.displayGraph();
    }

    //calculate shortest route using Dijkstra
    public void calculateDijkstraRoute(String source, String destination){

        //recreate Dijkstra with current graph 
        dijkstra = new Dijkstra(graph);

        List<String> route = dijkstra.findShortestRoute(source, destination);

        if(route.isEmpty()){
            System.out.println("No route found.");
            return;
        }

        double distance = dijkstra.getShortestDistance(source, destination);

        System.out.println("\n========== DIJKSTRA ROUTE ==========");

        System.out.println("Starting Location: " + source);

        System.out.println("Destination: " + destination);

        System.out.println("Shortest Route: ");

        for(int i=0; i<route.size(); i++){
            System.out.print(route.get(i));

            if (i < route.size()-1){
                System.out.println(" ->");
            }
        }

        System.out.println("\nTotal Distance: " + distance + " km");

        System.out.println("====================================");
    }

    //calculate all shortest paths using floydWarshall
    public void calculateFloyedWarshallRoutes(){

        floydWarshall = new FloydWarshall(graph);

        floydWarshall.calculateShortestPaths();

        floydWarshall.displayAllShortDistances();
    }

    //display shortest distance using floyed-warshall
    public void displayShortestRoute(String source, String destination){

        floydWarshall.displayShortestDistance(source, destination);
    }

    //get all locations
    public void displayAllLocationNames(){

        System.out.println("\nAvailable Locations: ");

        for(String location : graph.getLocations()){
            System.out.println("- " + location);
        }
    }

    //check whether a location exists
    public boolean locationExists(String location){
        return graph.containsLocation(location);
    }

    //get number of location
    public int getTotalLocations(){
        return graph.getLocationCount();
    }

    //get graph objects
    public Graph getGraph(){
        return graph;
    }
}
