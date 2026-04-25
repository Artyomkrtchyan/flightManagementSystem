package graph;

import model.Airport;
import model.Route;
import java.util.*;

/*
Time Complexity:
O((V + E) log V)
- V = number of airports
- E = number of routes
- Each node processed using priority queue (log V)
- Each edge relaxed once

Memory Complexity:
O(V)
- minCosts map stores best cost for each airport
- priority queue stores up to V nodes in worst case
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
                double price = route.getTicketPrice();
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

