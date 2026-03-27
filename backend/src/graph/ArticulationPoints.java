package graph;

import model.Airport;
import model.Route;
import java.util.*;

public class ArticulationPoints {

    public Set<Airport> findCriticalAirports(FlightGraph graph) {
        Map<Integer, Airport> airportMap = graph.getAirportMap();
        Set<Airport> critical = new HashSet<>();

        if (airportMap.size() < 3) return critical;

        for (Airport candidate : airportMap.values()) {
            int candidateId = candidate.getId();

            if (isActuallyCritical(graph, airportMap, candidateId)) {
                critical.add(candidate);
            }
        }
        return critical;
    }

    private boolean isActuallyCritical(FlightGraph graph, Map<Integer, Airport> airportMap, int skipId) {
        List<Integer> testNodes = new ArrayList<>(airportMap.keySet());
        Collections.shuffle(testNodes);

        int samples = Math.min(5, testNodes.size());

        for (int i = 0; i < samples; i++) {
            int startId = testNodes.get(i);
            if (startId == skipId) continue;

            Set<Integer> withNode = bfs(graph, airportMap, startId, -1);

            Set<Integer> withoutNode = bfs(graph, airportMap, startId, skipId);

            for (Integer reachedId : withNode) {
                if (reachedId != skipId && !withoutNode.contains(reachedId)) {
                    return true; 
                }
            }
        }
        return false;
    }

    private Set<Integer> bfs(FlightGraph graph, Map<Integer, Airport> airportMap, int startId, int skipId) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.add(startId);
        visited.add(startId);

        while (!queue.isEmpty()) {
            Integer currId = queue.poll();
            Airport curr = airportMap.get(currId);
            if (curr == null) continue;

            for (Route r : graph.getRoutesFrom(curr)) {
                int nextId = r.getDestinationAirportID();
                if (nextId != skipId && !visited.contains(nextId)) {
                    visited.add(nextId);
                    queue.add(nextId);
                }
            }
        }
        return visited;
    }
}
