package Src;

import Src.structures.Graph;

public class GraphTest {
    
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

        // Display road network
        graph.displayGraph();
    }

}
