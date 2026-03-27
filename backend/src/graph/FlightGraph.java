package graph;

import model.Airport;
import model.Route;
import java.util.*;

public class FlightGraph {
    // Карта для быстрого поиска: ID -> Объект Аэропорта
    private Map<Integer, Airport> airportMap = new HashMap<>();

    // Список смежности: ID аэропорта -> Список вылетающих маршрутов
    private Map<Integer, List<Route>> adjacencyList = new HashMap<>();

    // Общий список для вывода всех маршрутов на фронтенд
    private List<Route> allRoutes = new ArrayList<>();

    public void addRoute(Route route, Map<Integer, Airport> airports) {
        // Сохраняем ссылки на аэропорты, если их еще нет
        this.airportMap.putAll(airports);

        // Добавляем в общий список
        allRoutes.add(route);

        // Строим связи для алгоритмов (Дейкстры и др.)
        adjacencyList.computeIfAbsent(route.getSourceAirportID(), k -> new ArrayList<>()).add(route);
    }

    // Метод, который просит DijkstraFlexible
    public Map<Integer, Airport> getAirportMap() {
        return airportMap;
    }

    // Метод для получения маршрутов из конкретного аэропорта
    public List<Route> getRoutesFrom(Airport airport) {
        return adjacencyList.getOrDefault(airport.getId(), new ArrayList<>());
    }

    // Для сервера (список всех аэропортов)
    public Collection<Airport> getAirports() {
        return airportMap.values();
    }

    // Для сервера (список всех маршрутов)
    public List<Route> getRoutes() {
        return allRoutes;
    }
}