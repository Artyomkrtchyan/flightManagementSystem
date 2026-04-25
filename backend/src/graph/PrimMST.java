package graph;

import model.Airport;
import model.Route;

import java.util.*;

/*
Prim's Minimum Spanning Tree (MST):

Time Complexity:
O(E log V)
- V = number of airports (vertices)
- E = number of routes (edges)
- Each edge is processed via priority queue (log V)

Memory Complexity:
O(V + E)
- visited set stores up to V airports
- priority queue can store up to E edges
- graph storage is O(V + E)
*/

public class PrimMST {

    // Computes Minimum Spanning Tree (MST) using Prim's algorithm
    public static List<Route> computeMST(FlightGraph graph) {

        // Set of already included airports in MST
        Set<Airport> visited = new HashSet<>();

        // Result list of routes forming the MST
        List<Route> mstRoutes = new ArrayList<>();

        // Map of all airports for ID → object lookup
        Map<Integer, Airport> airportMap = graph.getAirportMap();

        // If graph is empty, return empty result
        if (airportMap.isEmpty()) return mstRoutes;

        // Start from any arbitrary airport
        Airport start = airportMap.values().iterator().next();
        visited.add(start);

        // Priority queue sorted by smallest distance (Greedy choice)
        PriorityQueue<Route> pq =
                new PriorityQueue<>(Comparator.comparingDouble(Route::getBaseDistanceKM));

        // Add all outgoing edges from start node
        pq.addAll(graph.getRoutesFrom(start));

        // Main loop: build MST
        while (!pq.isEmpty() && visited.size() < airportMap.size()) {

            // Pick the smallest edge
            Route route = pq.poll();

            Airport source = airportMap.get(route.getSourceAirportID());
            Airport dest = airportMap.get(route.getDestinationAirportID());

            // Choose the node that is not yet visited
            Airport next = !visited.contains(dest)
                    ? dest
                    : (!visited.contains(source) ? source : null);

            if (next == null) continue;

            // Add edge to MST result
            mstRoutes.add(route);

            // Mark node as visited
            visited.add(next);

            // Add all outgoing edges from newly visited node
            for (Route r : graph.getRoutesFrom(next)) {

                Airport rDest = airportMap.get(r.getDestinationAirportID());

                // Only consider edges leading to unvisited nodes
                if (!visited.contains(rDest)) {
                    pq.add(r);
                }
            }
        }

        return mstRoutes;
    }
}