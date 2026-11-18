package Src;

import Src.services.HomeVisitService;

public class HomeVisitServiceTest {
    
    public static void main(String[] args) {

        HomeVisitService homeVisit = new HomeVisitService();

        // Add locations
        homeVisit.addLocation("Kalmunai");
        homeVisit.addLocation("Maruthamunai");
        homeVisit.addLocation("Sainthamaruthu");
        homeVisit.addLocation("Addalaichenai");

        // Add roads
        homeVisit.addRoad("Kalmunai", "Maruthamunai", 5);
        homeVisit.addRoad("Kalmunai", "Sainthamaruthu", 3);
        homeVisit.addRoad("Maruthamunai", "Addalaichenai", 12);
        homeVisit.addRoad("Sainthamaruthu", "Addalaichenai", 8);

        // Display road network
        homeVisit.displayLocations();

        // Dijkstra algorithm
        System.out.println("\nDIJKSTRA ALGORITHM");

        homeVisit.calculateDijkstraRoute(
                "Kalmunai",
                "Addalaichenai"
        );

        // Floyd-Warshall algorithm
        System.out.println("\nFLOYD-WARSHALL ALGORITHM");

        homeVisit.calculateFloyedWarshallRoutes();

        homeVisit.displayShortestRoute(
                "Kalmunai",
                "Addalaichenai"
        );
    }

}
