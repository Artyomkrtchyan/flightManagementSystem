import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.io.OutputStream;
import java.sql.*;
import java.util.*;
import org.json.*;
import java.io.IOException;
import graph.ArticulationPoints;
import graph.BudgetFinder;
import java.nio.charset.StandardCharsets;
import graph.FlightGraph;
import graph.DijkstraFlexible;
import graph.PrimMST;
import graph.BFS;
import model.Airport;
import model.Route;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = connectDatabase()) {
            Map<Integer, Airport> airportMap = loadAirports(conn);
            FlightGraph flightGraph = new FlightGraph();
            loadRoutes(flightGraph, airportMap, conn);

            // Создаем объект здесь!
            DatabaseHelper dbHelper = new DatabaseHelper();

            // Передаем его в сервер
            startHttpServer(flightGraph, dbHelper,airportMap);

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Application Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendOptionsResponse(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }

    private static void startHttpServer(FlightGraph flightGraph, DatabaseHelper databaseHelper, Map<Integer, Airport> airportMap) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // Эндпоинт /graph - загрузка всех данных для карты
        server.createContext("/graph", exchange -> {
            try {
                JSONObject result = new JSONObject();
                JSONArray airports = new JSONArray();
                for (Airport a : airportMap.values()) {  // <-- используем все аэропорты
                    JSONObject obj = new JSONObject();
                    obj.put("airportID", a.getId());
                    obj.put("code", a.getCode());
                    obj.put("lat", a.getLat());
                    obj.put("lng", a.getLng());
                    obj.put("name", a.getCityName());
                    airports.put(obj);
                }
                result.put("airports", airports);

                JSONArray routes = new JSONArray();
                for (Route r : flightGraph.getRoutes()) {
                    JSONObject obj = new JSONObject();
                    obj.put("source", r.getSourceAirportID());
                    obj.put("destination", r.getDestinationAirportID());
                    obj.put("distance", r.getBaseDistanceKM());
                    routes.put(obj);
                }
                result.put("routes", routes);
                sendJsonResponse(exchange, result.toString());
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
                e.printStackTrace();
            }
        });

        server.createContext("/fastest", exchange -> {
            try {
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                int sourceId = Integer.parseInt(params.get("id"));
                int targetId = Integer.parseInt(params.get("to"));

                Airport source = flightGraph.getAirportMap().get(sourceId);
                Airport target = flightGraph.getAirportMap().get(targetId);

                DijkstraFlexible.DijkstraResult res = DijkstraFlexible.shortestPath(
                        flightGraph, source, Route::getBaseDistanceKM
                );

                JSONObject json = new JSONObject();
                json.put("path", new JSONArray(res.getPathTo(target)));
                Double dist = res.distances.get(target);
                json.put("totalDistance", (dist == null || dist == Double.POSITIVE_INFINITY) ? 0 : dist);

                sendJsonResponse(exchange, json.toString());
            } catch (Exception e) {
                exchange.sendResponseHeaders(400, 0);
            }
        });

        // Эндпоинт /cheapest - Самый дешевый путь
        server.createContext("/cheapest", exchange -> {
            try {
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                int sourceId = Integer.parseInt(params.get("id"));
                int targetId = Integer.parseInt(params.get("to"));

                Airport source = flightGraph.getAirportMap().get(sourceId);
                Airport target = flightGraph.getAirportMap().get(targetId);

                DijkstraFlexible.DijkstraResult res = DijkstraFlexible.shortestPath(
                        flightGraph, source, route -> (double) route.getTicketPrice()
                );

                JSONObject json = new JSONObject();
                json.put("path", new JSONArray(res.getPathTo(target)));
                Double price = res.distances.get(target);
                json.put("totalPrice", (price == null || price == Double.POSITIVE_INFINITY) ? 0 : price);

                sendJsonResponse(exchange, json.toString());
            } catch (Exception e) {
                exchange.sendResponseHeaders(400, 0);
            }
        });

        // --- НОВЫЙ ЭНДПОИНТ: BFS ---
        server.createContext("/bfs", exchange -> {
            try {
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                int sourceId = Integer.parseInt(params.get("id"));
                int k = Integer.parseInt(params.getOrDefault("k", "1"));

                Airport source = flightGraph.getAirportMap().get(sourceId);
                if (source == null) {
                    sendJsonResponse(exchange, "{\"reachableIds\":[], \"usedRoutes\":[]}");
                    return;
                }

                // --- ЛОГИКА BFS ПРЯМО ЗДЕСЬ ---
                Set<Integer> visited = new HashSet<>();
                JSONArray usedRoutes = new JSONArray();
                JSONArray reachableIds = new JSONArray();

                Queue<Airport> queue = new LinkedList<>();
                Map<Integer, Integer> levels = new HashMap<>();

                queue.add(source);
                visited.add(source.getId());
                levels.put(source.getId(), 0);

                while (!queue.isEmpty()) {
                    Airport current = queue.poll();
                    int currentLevel = levels.get(current.getId());

                    if (currentLevel < k) {
                        for (Route route : flightGraph.getRoutesFrom(current)) {
                            int destId = route.getDestinationAirportID();
                            if (!visited.contains(destId)) {
                                visited.add(destId);
                                levels.put(destId, currentLevel + 1);
                                queue.add(flightGraph.getAirportMap().get(destId));

                                // Добавляем ID аэропорта
                                reachableIds.put(destId);

                                // Добавляем маршрут (линию)
                                JSONObject routeObj = new JSONObject();
                                routeObj.put("source", current.getId());
                                routeObj.put("destination", destId);
                                usedRoutes.put(routeObj);
                            }
                        }
                    }
                }

                JSONObject json = new JSONObject();
                json.put("reachableIds", reachableIds);
                json.put("usedRoutes", usedRoutes); // ТЕПЕРЬ ФРОНТ УВИДИТ ЛИНИИ

                sendJsonResponse(exchange, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(400, 0);
            }
        });

        server.createContext("/critical", exchange -> {
            try {
                ArticulationPoints apFinder = new ArticulationPoints();
                Set<Airport> critical = apFinder.findCriticalAirports(flightGraph);

                JSONArray criticalIds = new JSONArray();
                for (Airport a : critical) {
                    criticalIds.put(a.getId());
                }

                JSONObject json = new JSONObject();
                json.put("criticalIds", criticalIds);
                sendJsonResponse(exchange, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        });

        // Эндпоинт /mst - расчет минимального остовного дерева
        server.createContext("/mst", exchange -> {
            try {
                List<Route> mst = PrimMST.computeMST(flightGraph);

                JSONArray routes = new JSONArray();
                for (Route r : mst) {
                    JSONObject obj = new JSONObject();
                    obj.put("source", r.getSourceAirportID());
                    obj.put("destination", r.getDestinationAirportID());
                    obj.put("distance", r.getBaseDistanceKM());
                    routes.put(obj);
                }

                JSONObject json = new JSONObject();
                json.put("mstRoutes", routes);
                sendJsonResponse(exchange, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        });

        server.createContext("/budget", exchange -> {
            try {
                // Убедись, что метод queryToMap доступен в этом классе
                Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());

                if (params.get("id") == null || params.get("maxBudget") == null) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                int startId = Integer.parseInt(params.get("id"));
                double maxBudget = Double.parseDouble(params.get("maxBudget"));

                BudgetFinder budgetFinder = new BudgetFinder();
                // Используем flightGraph, переданный в параметры startHttpServer
                Set<Integer> reachableIds = budgetFinder.findAirportsWithinBudget(flightGraph, startId, maxBudget);

                JSONObject json = new JSONObject();
                json.put("reachableIds", new JSONArray(reachableIds));

                // Исправлено: вызываем правильный метод отправки
                sendJsonResponse(exchange, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    exchange.sendResponseHeaders(500, -1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } finally {
                exchange.close();
            }
        });

        server.createContext("/api/tables", exchange -> {
            String json = new JSONArray(databaseHelper.getAllTableNames()).toString();
            sendJsonResponse(exchange, json);
        });

        server.createContext("/api/data", exchange -> {
            try {
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                String table = params.get("table");

                if (table == null || table.isEmpty()) {
                    exchange.sendResponseHeaders(400, 0);
                    return;
                }

                String json = databaseHelper.fetchTable(table);
                sendJsonResponse(exchange, json); // Используем твой готовый метод для отправки
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        });

        // Удаление записи
        server.createContext("/api/delete", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                sendOptionsResponse(exchange);
                return;
            }
            try {
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
                String table = params.get("table");
                String column = params.get("col");
                int id = Integer.parseInt(params.get("id"));

                boolean deleted = databaseHelper.deleteRow(table, column, id);
                sendJsonResponse(exchange, "{\"success\":" + deleted + "}");
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, 0);
            }
        });

        server.createContext("/api/add", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                sendOptionsResponse(exchange);
                return;
            }

            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Читаем JSON из тела запроса
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject jsonBody = new JSONObject(body);

                    String table = jsonBody.getString("table");
                    JSONObject data = jsonBody.getJSONObject("data");

                    boolean success = databaseHelper.addRow(table, data);
                    sendJsonResponse(exchange, "{\"success\":" + success + "}");
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
            } else {
                exchange.sendResponseHeaders(405, 0); // Метод не позволен
            }
        });




        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8081...");
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) result.put(entry[0], entry[1]);
        }
        return result;
    }

    private static void sendJsonResponse(HttpExchange exchange, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Connection connectDatabase() throws SQLException {

        String url = "jdbc:sqlserver://localhost:65057;databaseName=Flights;encrypt=true;trustServerCertificate=true";
        return DriverManager.getConnection(url, "sa", "burunduk");
    }

    private static Map<Integer, Airport> loadAirports(Connection conn) throws SQLException {
        Map<Integer, Airport> airportMap = new HashMap<>();
        String sql = "SELECT a.AirportID, a.Code, a.AirportName, a.Latitude, a.Longitude, c.CityName " +
                "FROM Airports a JOIN Cities c ON a.CityID = c.CityID";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("AirportID");
                airportMap.put(id, new Airport(id, rs.getString("Code"), rs.getString("AirportName"),
                        rs.getDouble("Latitude"), rs.getDouble("Longitude"), rs.getString("CityName")));
            }
        }
        return airportMap;
    }

    private static void loadRoutes(FlightGraph flightGraph, Map<Integer, Airport> airportMap, Connection conn) throws SQLException {
        String sql = "SELECT RouteID, SourceAirportID, DestinationAirportID, BaseDistanceKM, Cost FROM Routes";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Route route = new Route(rs.getInt("RouteID"), rs.getInt("SourceAirportID"),
                        rs.getInt("DestinationAirportID"), rs.getDouble("BaseDistanceKM"), rs.getDouble("Cost"));
                flightGraph.addRoute(route, airportMap);
            }
        }
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return result;
        }
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }


}