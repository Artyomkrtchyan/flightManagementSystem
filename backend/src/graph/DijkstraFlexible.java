package graph;

import model.Airport;
import model.Route;
import java.util.*;
import java.util.function.ToDoubleFunction;

/*
Dijkstra Algorithm Complexity:

Time Complexity:
O((V + E) log V)
- V = number of airports (vertices)
- E = number of routes (edges)
- log V comes from priority queue operations

Memory Complexity:
O(V + E)
- O(V) for distance and parent maps
- O(E) for graph storage (routes)
- O(V) for priority queue in worst case
*/


public class DijkstraFlexible {

    // This class stores the result of Dijkstra algorithm
    public static class DijkstraResult {

        // Shortest distances from source to all airports
        public Map<Airport, Double> distances;

        // Stores the previous node for path reconstruction
        public Map<Airport, Airport> parents;

        public DijkstraResult(Map<Airport, Double> distances, Map<Airport, Airport> parents) {
            this.distances = distances;
            this.parents = parents;
        }

        // Reconstructs the shortest path from source to target
        public List<Integer> getPathTo(Airport target) {
            List<Integer> path = new ArrayList<>();

            // Go backwards using parent pointers
            for (Airport at = target; at != null; at = parents.get(at)) {
                path.add(at.getId());
            }

            // Reverse to get correct order (source → target)
            Collections.reverse(path);
            return path;
        }
    }

    // Generic Dijkstra algorithm (works with different weights)
    public static DijkstraResult shortestPath(
            FlightGraph graph,
            Airport source,
            ToDoubleFunction<Route> weightFunc
    ) {

        // Get all airports from graph
        Map<Integer, Airport> airportMap = graph.getAirportMap();

        // Distance map (Airport -> current shortest distance)
        Map<Airport, Double> dist = new HashMap<>();

        // Parent map for path reconstruction
        Map<Airport, Airport> parentMap = new HashMap<>();

        // Initialize all distances to infinity
        for (Airport a : airportMap.values())
            dist.put(a, Double.POSITIVE_INFINITY);

        // Distance to source is 0
        dist.put(source, 0.0);

        // Priority queue (min-heap) based on distance
        PriorityQueue<AirportDistance> pq =
                new PriorityQueue<>(Comparator.comparingDouble(ad -> ad.distance));

        pq.add(new AirportDistance(source, 0));

        // Main Dijkstra loop
        while (!pq.isEmpty()) {

            AirportDistance current = pq.poll();

            // Skip outdated entries (lazy deletion optimization)
            if (current.distance > dist.get(current.airport)) continue;

            // Traverse all outgoing routes
            for (Route route : graph.getRoutesFrom(current.airport)) {

                Airport dest = airportMap.get(route.getDestinationAirportID());
                if (dest == null) continue;

                // Calculate new distance using chosen weight function
                double newDist = dist.get(current.airport)
                        + weightFunc.applyAsDouble(route);

                // Relaxation step
                if (newDist < dist.get(dest)) {
                    dist.put(dest, newDist);
                    parentMap.put(dest, current.airport);

                    pq.add(new AirportDistance(dest, newDist));
                }
            }
        }

        return new DijkstraResult(dist, parentMap);
    }

    // Helper class for priority queue
    private static class AirportDistance {
        Airport airport;
        double distance;

        AirportDistance(Airport airport, double distance) {
            this.airport = airport;
            this.distance = distance;
        }
    }
}