package graph;

import model.Airport;
import model.Route;
import java.util.*;
import java.util.function.ToDoubleFunction;

public class DijkstraFlexible {

    public static class DijkstraResult {
        public Map<Airport, Double> distances;
        public Map<Airport, Airport> parents;

        public DijkstraResult(Map<Airport, Double> distances, Map<Airport, Airport> parents) {
            this.distances = distances;
            this.parents = parents;
        }

        // Вспомогательный метод для получения списка ID аэропортов в пути
        public List<Integer> getPathTo(Airport target) {
            List<Integer> path = new ArrayList<>();
            for (Airport at = target; at != null; at = parents.get(at)) {
                path.add(at.getId());
            }
            Collections.reverse(path);
            return path;
        }
    }

    public static DijkstraResult shortestPath(
            FlightGraph graph,
            Airport source,
            ToDoubleFunction<Route> weightFunc
    ) {
        Map<Integer, Airport> airportMap = graph.getAirportMap();
        Map<Airport, Double> dist = new HashMap<>();
        Map<Airport, Airport> parentMap = new HashMap<>(); // Храним путь 🚩

        for (Airport a : airportMap.values()) dist.put(a, Double.POSITIVE_INFINITY);
        dist.put(source, 0.0);

        PriorityQueue<AirportDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(ad -> ad.distance));
        pq.add(new AirportDistance(source, 0));

        while (!pq.isEmpty()) {
            AirportDistance current = pq.poll();
            if (current.distance > dist.get(current.airport)) continue;

            for (Route route : graph.getRoutesFrom(current.airport)) {
                Airport dest = airportMap.get(route.getDestinationAirportID());
                if (dest == null) continue;

                double newDist = dist.get(current.airport) + weightFunc.applyAsDouble(route);
                if (newDist < dist.get(dest)) {
                    dist.put(dest, newDist);
                    parentMap.put(dest, current.airport); // Запоминаем, что в dest пришли из current
                    pq.add(new AirportDistance(dest, newDist));
                }
            }
        }
        return new DijkstraResult(dist, parentMap);
    }

    private static class AirportDistance {
        Airport airport;
        double distance;
        AirportDistance(Airport airport, double distance) {
            this.airport = airport;
            this.distance = distance;
        }
    }
}