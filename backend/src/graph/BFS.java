package graph;

import model.Airport;
import model.Route;

import java.util.*;

/*
Time Complexity:
O(V + E)
- V = number of airports (nodes)
- E = number of routes (edges)
- Each airport is visited once
- Each route is checked once

Memory Complexity:
O(V)
- visited set stores up to V airports
- queue stores up to V airports in worst case
- levelMap stores up to V entries
*/

public class BFS {

    // Returns all airports reachable from source within K steps (levels)
    public static Set<Airport> reachableWithinK(FlightGraph graph, Airport source, int K) {

        // Map of all airports for quick ID → object lookup
        Map<Integer, Airport> airportMap = graph.getAirportMap();

        // Stores visited airport IDs to avoid revisiting
        Set<Integer> visitedIds = new HashSet<>();

        // BFS queue
        Queue<Airport> queue = new LinkedList<>();

        // Stores level (distance in edges) from source
        Map<Integer, Integer> levelMap = new HashMap<>();

        // If source is null, return empty result
        if (source == null) return Collections.emptySet();

        // Initialize BFS
        queue.add(source);
        levelMap.put(source.getId(), 0);
        visitedIds.add(source.getId());

        // Standard BFS loop
        while (!queue.isEmpty()) {

            Airport current = queue.poll();
            int currentLevel = levelMap.get(current.getId());

            // Stop expanding if we reached max depth K
            if (currentLevel >= K) continue;

            // Explore all outgoing routes
            for (Route route : graph.getRoutesFrom(current)) {

                int destId = route.getDestinationAirportID();

                // If not visited yet
                if (!visitedIds.contains(destId)) {

                    Airport dest = airportMap.get(destId);

                    if (dest != null) {
                        visitedIds.add(destId);

                        // Set level (distance in number of flights)
                        levelMap.put(destId, currentLevel + 1);

                        queue.add(dest);
                    }
                }
            }
        }
        // Convert visited IDs back to Airport objects
        Set<Airport> result = new HashSet<>();
        for (Integer id : visitedIds) {
            if (id != source.getId()) {
                result.add(airportMap.get(id));
            }
        }
        return result;
    }
}