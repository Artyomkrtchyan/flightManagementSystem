package graph;

import model.Airport;
import model.Route;

import java.util.*;

/**
 * Compute Minimum Spanning Tree (MST) using Prim's Algorithm.
 * Time Complexity: O(E log V) using PriorityQueue
 * Memory Complexity: O(V + E)
 */
public class PrimMST {

    public static List<Route> computeMST(FlightGraph graph) {
        Set<Airport> visited = new HashSet<>();
        List<Route> mstRoutes = new ArrayList<>();

        Map<Integer, Airport> airportMap = graph.getAirportMap();
        if (airportMap.isEmpty()) return mstRoutes;

        // Start from any airport
        Airport start = airportMap.values().iterator().next();
        visited.add(start);

        // PriorityQueue: smallest distance first
        PriorityQueue<Route> pq = new PriorityQueue<>(Comparator.comparingDouble(Route::getBaseDistanceKM));
        pq.addAll(graph.getRoutesFrom(start));

        while (!pq.isEmpty() && visited.size() < airportMap.size()) {
            Route route = pq.poll();
            Airport source = airportMap.get(route.getSourceAirportID());
            Airport dest = airportMap.get(route.getDestinationAirportID());

            // Choose the airport not yet visited
            Airport next = !visited.contains(dest) ? dest : (!visited.contains(source) ? source : null);
            if (next == null) continue;

            mstRoutes.add(route);
            visited.add(next);

            // Add all edges from next airport to PQ
            for (Route r : graph.getRoutesFrom(next)) {
                Airport rDest = airportMap.get(r.getDestinationAirportID());
                if (!visited.contains(rDest)) {
                    pq.add(r);
                }
            }
        }

        return mstRoutes;
    }
}