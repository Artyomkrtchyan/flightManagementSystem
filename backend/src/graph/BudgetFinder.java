package graph;

import model.Airport;
import model.Route;
import java.util.*;

/**
 * Поиск всех направлений, доступных в рамках заданного бюджета.
 * Сложность: O(E log V) по времени, O(V + E) по памяти.
 */
public class BudgetFinder {

    public Set<Integer> findAirportsWithinBudget(FlightGraph graph, int startId, double maxBudget) {
        Map<Integer, Airport> airportMap = graph.getAirportMap();

        // Храним минимальную стоимость достижения каждого узла
        Map<Integer, Double> minCosts = new HashMap<>();

        // PriorityQueue для выбора узла с наименьшей накопленной стоимостью
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));

        pq.add(new Node(startId, 0.0));
        minCosts.put(startId, 0.0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // Если стоимость уже превышает лимит, ветка дальше не исследуется
            if (current.cost > maxBudget) continue;

            Airport currentAirport = airportMap.get(current.id);
            if (currentAirport == null) continue;

            // Проходим по всем исходящим рейсам
            for (Route route : graph.getRoutesFrom(currentAirport)) {
                int nextId = route.getDestinationAirportID();
                // Предполагается, что в классе Route есть метод getPrice() или getDistance() как мера стоимости
                double price = route.getTicketPrice(); // Замени на route.getPrice(), если есть такое поле
                double newTotalCost = current.cost + price;

                if (newTotalCost <= maxBudget && newTotalCost < minCosts.getOrDefault(nextId, Double.MAX_VALUE)) {
                    minCosts.put(nextId, newTotalCost);
                    pq.add(new Node(nextId, newTotalCost));
                }
            }
        }

        // Удаляем стартовый аэропорт из результата и возвращаем ID достижимых
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

