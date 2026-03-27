package graph;

import model.Airport;
import model.Route;
import java.util.*;

public class FlightGraph {
    private Map<Integer, Airport> airportMap = new HashMap<>();

    private Map<Integer, List<Route>> adjacencyList = new HashMap<>();

    private List<Route> allRoutes = new ArrayList<>();

    public void addRoute(Route route, Map<Integer, Airport> airports) {
        this.airportMap.putAll(airports);

        allRoutes.add(route);

        adjacencyList.computeIfAbsent(route.getSourceAirportID(), k -> new ArrayList<>()).add(route);
    }

    public Map<Integer, Airport> getAirportMap() {
        return airportMap;
    }

    public List<Route> getRoutesFrom(Airport airport) {
        return adjacencyList.getOrDefault(airport.getId(), new ArrayList<>());
    }

    public Collection<Airport> getAirports() {
        return airportMap.values();
    }

    public List<Route> getRoutes() {
        return allRoutes;
    }
}
