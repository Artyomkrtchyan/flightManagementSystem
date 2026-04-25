package graph;

import model.Airport;
import model.Route;
import java.util.*;

/*
FlightGraph Data Structure:

Time Complexity:
- addRoute: O(1)
- getRoutesFrom: O(1)
- getAirports: O(1)
- getRoutes: O(1)

Memory Complexity:
O(V + E)
- V = number of airports stored in airportMap
- E = number of routes stored in adjacencyList + allRoutes

Representation:
- Adjacency List
- airportMap: vertex storage
- adjacencyList: outgoing edges per vertex
*/

public class FlightGraph {

    // Stores all airports in the system (key = airport ID, value = Airport object)
    private Map<Integer, Airport> airportMap = new HashMap<>();

    // Adjacency list representation of the graph:
    // key = source airport ID, value = list of outgoing routes
    private Map<Integer, List<Route>> adjacencyList = new HashMap<>();

    // List of all routes (edges) in the graph
    private List<Route> allRoutes = new ArrayList<>();

    // Adds a route to the graph
    public void addRoute(Route route, Map<Integer, Airport> airports) {

        // Load all airports into the internal map
        this.airportMap.putAll(airports);

        // Store the route in the global list of all routes
        allRoutes.add(route);

        // Add the route to adjacency list (graph structure)
        // If no list exists for this source airport, create a new one
        adjacencyList
                .computeIfAbsent(route.getSourceAirportID(), k -> new ArrayList<>())
                .add(route);
    }

    // Returns map of all airports
    public Map<Integer, Airport> getAirportMap() {
        return airportMap;
    }

    // Returns all outgoing routes from a specific airport
    public List<Route> getRoutesFrom(Airport airport) {
        return adjacencyList.getOrDefault(
                airport.getId(),
                new ArrayList<>()
        );
    }

    // Returns all airports in the graph
    public Collection<Airport> getAirports() {
        return airportMap.values();
    }

    // Returns all routes (edges) in the graph
    public List<Route> getRoutes() {
        return allRoutes;
    }
}