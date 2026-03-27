package graph;

import model.Airport;
import model.Route;
import java.util.*;

public class ArticulationPoints {

    public Set<Airport> findCriticalAirports(FlightGraph graph) {
        Map<Integer, Airport> airportMap = graph.getAirportMap();
        Set<Airport> critical = new HashSet<>();

        if (airportMap.size() < 3) return critical;

        // Проверяем каждый аэропорт по очереди
        for (Airport candidate : airportMap.values()) {
            int candidateId = candidate.getId();

            // Проверяем: есть ли хотя бы одна пара (A, B), путь между которыми
            // существовал, но пропал после удаления candidate?
            if (isActuallyCritical(graph, airportMap, candidateId)) {
                critical.add(candidate);
            }
        }
        return critical;
    }

    private boolean isActuallyCritical(FlightGraph graph, Map<Integer, Airport> airportMap, int skipId) {
        // Выбираем несколько случайных "контрольных" точек для проверки связности
        // Это быстрее и точнее для ориентированных графов
        List<Integer> testNodes = new ArrayList<>(airportMap.keySet());
        Collections.shuffle(testNodes);

        // Берем первые 5 узлов как источники (для скорости)
        int samples = Math.min(5, testNodes.size());

        for (int i = 0; i < samples; i++) {
            int startId = testNodes.get(i);
            if (startId == skipId) continue;

            // 1. Считаем достижимость С узлом
            Set<Integer> withNode = bfs(graph, airportMap, startId, -1);

            // 2. Считаем достижимость БЕЗ узла
            Set<Integer> withoutNode = bfs(graph, airportMap, startId, skipId);

            // Если без узла мы потеряли доступ к какой-то части графа,
            // которую видели раньше (и это не сам удаленный узел)
            for (Integer reachedId : withNode) {
                if (reachedId != skipId && !withoutNode.contains(reachedId)) {
                    return true; // Узел реально критический
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