package Src.services;

import Src.structures.Graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Src.algorithms.Dijkstra;
import Src.algorithms.FloydWarshall;

public class HomeVisitService {
    
    //declare attributes
    private Graph graph;
    private Dijkstra dijkstra;
    private FloydWarshall floydWarshall;

    private final String filePath = "Data/home_visits.txt";

    //set constructor
    public HomeVisitService(){

        graph = new Graph();

        loadHomeVisitData();

        dijkstra = new Dijkstra(graph);
        floydWarshall = new FloydWarshall(graph);
    }

    //add a new location
    public void addLocation(String location){

        if(location == null || location.trim().isEmpty()){

            System.out.println("Invalid location name.");
            return ;
        }

        if(graph.containsLocation(location)){
            System.out.println("Location already exists.");
            return ;
        }

        graph.addLocation(location);
        saveHomeVisitData();

        System.out.println("Location added successfully.");
    }

    //add a road between two location
    public void addRoad(String source, String destination, double distance){

        if(!graph.containsLocation(source) || !graph.containsLocation(destination)){

            System.out.println("Source or destination location not found.");
            return ;
        }

        if(distance <=0 || !Double.isFinite(distance) ){

            System.out.println("Distance must be positive number.");
            return ;
        }

        if(graph.hasRoad(source, destination)){
            System.out.println("Road already exists.");
            return ;
        }

        graph.addRoad(source, destination, distance);
        saveHomeVisitData();

        System.out.println("Road added successfully.");
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

        floydWarshall = new FloydWarshall(graph);
        floydWarshall.calculateShortestPaths();
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

    //save all location and roads to the text file
    private void saveHomeVisitData(){

        Path path = Paths.get(filePath);

        try{

            //ensure the data folder exists
            Files.createDirectories(path.getParent());

            try(BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)){

                //save location
                for(String location : graph.getLocations()){

                    writer.write("LOCATION|" + location);
                    writer.newLine();
                }

                //save roads
                Set<String> saveRoads = new HashSet<>();

                for(String source : graph.getLocations()){

                    for(Graph.Edge edge : graph.getNeighbours(source)){

                        String destination = edge.getDestination();

                        String first = source.compareTo(destination) < 0? source : destination;

                        String second = source.compareTo(destination) <0? destination : source;

                        String roadKey = first + "|" + second;

                        if(saveRoads.add(roadKey)){
                            writer.write("ROAD|" + source + "|" + destination + "|" + edge.getDistance());

                            writer.newLine();
                        }
                    }
                }
            }
        }

        catch(IOException e){
            System.out.println("Error saveing home_Visit data: " + e.getMessage());
        }
    }

    //load locations and roads from the database
    private void loadHomeVisitData(){

        Path path = Paths.get(filePath);

        if(!Files.exists(path)){
            System.out.println("No previous home visit data found.");
            return ;
        }

        try(BufferedReader reader = Files.newBufferedReader(path)){

            String line;

            while((line = reader.readLine()) != null){

                String[] data = line.split("\\|", -1);

                try{

                    //load location
                    if (data.length == 2 && data[0].equals("LOCATION")){

                        String location = data[1];

                        if(!location.trim().isEmpty() && !graph.containsLocation(location)){

                            graph.addLocation(location);
                        }
                    }

                    //load road
                    else if(data.length == 4 && data[0].equals("ROAD")){

                        String source = data[1];
                        String destination = data[2];
                        Double distance = Double.parseDouble(data[3]);

                        if(source.trim().isEmpty() || destination.trim().isEmpty() || distance <= 0 || !Double.isFinite(distance)){

                            System.out.println("Invalid road records skipped: " + line);
                            continue;
                        }

                        //ensure both location exists
                        if(!graph.containsLocation(source)){

                            graph.addLocation(source);
                        }

                        if(!graph.containsLocation(destination)){

                            graph.addLocation(destination);
                        }

                        if(!graph.hasRoad(source, destination)){

                            graph.addRoad(source, destination, distance);
                        }
                    }

                    else{

                        System.out.println("Invalid home visit record skipped: " + line);
                    }
                }

                catch(IllegalArgumentException e){

                    System.out.println("Invalid record skipped: " + line);
                }
            }

            System.out.println("Home visit data loaded successfully.");
        }
        catch(IOException e) {
            System.out.println("Error loading home_visit data: " + e.getMessage());
        }
    }
}
