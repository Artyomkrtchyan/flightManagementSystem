package graph;

import model.Airport;
import model.Route;

import java.util.*;

/**
 * BFS for airports reachable within at most K connections
 * Uses Map<Integer, Airport> for O(1) lookup
 * Time: O(V + E), Memory: O(V + E)
 */
public class BFS {

    public static Set<Airport> reachableWithinK(FlightGraph graph, Airport source, int K) {
        Map<Integer, Airport> airportMap = graph.getAirportMap();

        // Используем ID (Integer) вместо объектов Airport для надежности
        Set<Integer> visitedIds = new HashSet<>();
        Queue<Airport> queue = new LinkedList<>();
        Map<Integer, Integer> levelMap = new HashMap<>();

        if (source == null) return Collections.emptySet();

        queue.add(source);
        levelMap.put(source.getId(), 0);
        visitedIds.add(source.getId());

        while (!queue.isEmpty()) {
            Airport current = queue.poll();
            int currentLevel = levelMap.get(current.getId());

            // Если мы достигли лимита пересадок, дальше этого узла не идем
            if (currentLevel >= K) continue;

            for (Route route : graph.getRoutesFrom(current)) {
                int destId = route.getDestinationAirportID();

                if (!visitedIds.contains(destId)) {
                    Airport dest = airportMap.get(destId);
                    if (dest != null) {
                        visitedIds.add(destId);
                        levelMap.put(destId, currentLevel + 1);
                        queue.add(dest);
                    }
                }
            }
        }

        // Собираем результат обратно в Set<Airport>
        Set<Airport> result = new HashSet<>();
        for (Integer id : visitedIds) {
            if (id != source.getId()) {
                result.add(airportMap.get(id));
            }
        }
        return result;
    }
}