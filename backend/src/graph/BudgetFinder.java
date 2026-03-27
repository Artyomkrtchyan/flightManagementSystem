package graph;

import model.Airport;
import model.Route;
import java.util.*;

/**
 * Complexity: O(E log V) Time, O(V + E) Memory.
 */
public class BudgetFinder {

    public Set<Integer> findAirportsWithinBudget(FlightGraph graph, int startId, double maxBudget) {
        Map<Integer, Airport> airportMap = graph.getAirportMap();

        Map<Integer, Double> minCosts = new HashMap<>();

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));

        pq.add(new Node(startId, 0.0));
        minCosts.put(startId, 0.0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            if (current.cost > maxBudget) continue;

            Airport currentAirport = airportMap.get(current.id);
            if (currentAirport == null) continue;

            for (Route route : graph.getRoutesFrom(currentAirport)) {
                int nextId = route.getDestinationAirportID();
                double price = route.getTicketPrice(); // Замени на route.getPrice(), если есть такое поле
                double newTotalCost = current.cost + price;

                if (newTotalCost <= maxBudget && newTotalCost < minCosts.getOrDefault(nextId, Double.MAX_VALUE)) {
                    minCosts.put(nextId, newTotalCost);
                    pq.add(new Node(nextId, newTotalCost));
                }
            }
        }

        Set<Integer> result = new HashSet<>(minCosts.keySet());
        result.remove(startId);
        return result;
    }

    private static class Node {
        int id;
        double cost;

        Node(int id, double cost) {
            this.id = id;
            this.cost = cost;
        }
    }
}

