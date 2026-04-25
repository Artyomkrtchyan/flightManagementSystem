package graph;

import model.Airport;
import model.Route;
import java.util.*;

/*
 * Critical Airports Detection via Tarjan's SCC + Condensation Analysis
 *
 * Definition: Airport X is critical if removing it makes at least one
 * other airport completely unreachable from the rest of the graph.
 *
 * Algorithm:
 *   1. Find all SCCs using Tarjan's DFS — O(V + E)
 *   2. Build condensation DAG, track which airports provide each cross-SCC edge — O(V + E)
 *   3. An airport is critical if it is the sole provider of the only
 *      incoming edge to some target SCC — O(SCC_count) ≤ O(V)
 *
 * Time:   O(V + E) — two linear passes, no repeated traversals
 * Memory: O(V + E) — disc[], low[], onStack[], sccId[], condensation edge maps
 */
public class ArticulationPoints {

    private int timer = 0;

    public Set<Airport> findCriticalAirports(FlightGraph graph) {
        Map<Integer, Airport> airportMap = graph.getAirportMap();
        Set<Airport> critical = new HashSet<>();

        if (airportMap.size() < 2) return critical;

        // ---------------------------------------------------------------
        // Step 1: Tarjan's SCC — single DFS pass
        // ---------------------------------------------------------------
        // disc[v]    = discovery timestamp when DFS first visits v
        // low[v]     = lowest disc reachable from subtree of v via back edges
        // onStack[v] = true while v is on the DFS stack (part of current SCC candidate)
        // sccId[v]   = which SCC node v belongs to (assigned after SCC is closed)
        // sccs       = list of SCCs, each SCC is a list of node IDs

        Map<Integer, Integer> disc    = new HashMap<>();
        Map<Integer, Integer> low     = new HashMap<>();
        Map<Integer, Boolean> onStack = new HashMap<>();
        Deque<Integer> stack          = new ArrayDeque<>();
        Map<Integer, Integer> sccId   = new HashMap<>();
        List<List<Integer>> sccs      = new ArrayList<>();

        for (int id : airportMap.keySet()) {
            if (!disc.containsKey(id)) {
                tarjanDFS(id, graph, airportMap, disc, low, onStack, stack, sccId, sccs);
            }
        }

        int sccCount = sccs.size();

        // If entire graph is one SCC, every node is mutually reachable —
        // removing any single node cannot disconnect the rest
        if (sccCount == 1) return critical;

        // ---------------------------------------------------------------
        // Step 2: Build condensation in one pass over all edges — O(V + E)
        // ---------------------------------------------------------------
        // sccInSources[t]         = set of SCC indices that have at least one edge INTO scc t
        //                           used to check if scc t has a unique incoming SCC
        //
        // edgeProviders[t][s]     = set of airport IDs inside scc s that provide
        //                           at least one direct edge into scc t
        //                           used to check if a single airport is the sole bridge

        Map<Integer, Set<Integer>>              sccInSources  = new HashMap<>();
        Map<Integer, Map<Integer, Set<Integer>>> edgeProviders = new HashMap<>();

        for (int i = 0; i < sccCount; i++) {
            sccInSources.put(i, new HashSet<>());
            edgeProviders.put(i, new HashMap<>());
        }

        for (int uId : airportMap.keySet()) {
            Airport u = airportMap.get(uId);
            if (u == null) continue;

            int sccU = sccId.getOrDefault(uId, -1);

            for (Route r : graph.getRoutesFrom(u)) {
                int vId  = r.getDestinationAirportID();
                int sccV = sccId.getOrDefault(vId, -1);

                // Skip self-loops within the same SCC and unknown nodes
                if (sccU < 0 || sccV < 0 || sccU == sccV) continue;

                // Record that sccU is a source for sccV
                sccInSources.get(sccV).add(sccU);

                // Record that airport uId provides this cross-SCC edge
                edgeProviders.get(sccV)
                        .computeIfAbsent(sccU, k -> new HashSet<>())
                        .add(uId);
            }
        }

        // ---------------------------------------------------------------
        // Step 3: Identify critical airports from condensation — O(SCC_count)
        // ---------------------------------------------------------------
        // Airport X is critical if and only if:
        //   ∃ target SCC T such that:
        //     (a) T has exactly one incoming SCC source S  → sccInSources[T].size() == 1
        //     (b) X is the only airport in S providing edges to T → edgeProviders[T][S].size() == 1
        //
        // If (a) fails: T has multiple source SCCs, so removing X still leaves T reachable
        // If (b) fails: multiple airports in S cover the link, so removing X doesn't isolate T

        for (int targetScc = 0; targetScc < sccCount; targetScc++) {
            Set<Integer> sources = sccInSources.get(targetScc);

            // Condition (a): exactly one SCC feeds into this target
            if (sources.size() != 1) continue;

            int soleScc = sources.iterator().next();
            Set<Integer> providers = edgeProviders.get(targetScc).get(soleScc);

            if (providers == null) continue;

            // Condition (b): exactly one airport provides the only incoming link
            if (providers.size() == 1) {
                int criticalId = providers.iterator().next();
                Airport a = airportMap.get(criticalId);
                if (a != null) critical.add(a);
            }
        }

        return critical;
    }

    // ---------------------------------------------------------------
    // Tarjan's DFS — iterative to avoid stack overflow on large graphs
    // ---------------------------------------------------------------
    // Uses an explicit call stack instead of JVM recursion.
    // Each frame stores: node id, iterator over its routes, and
    // whether the frame has been initialized.
    //
    // SCC is closed when low[u] == disc[u] (u is the root of its SCC).
    // All nodes above u on the stack belong to the same SCC.

    private void tarjanDFS(
            int startId,
            FlightGraph graph,
            Map<Integer, Airport> airportMap,
            Map<Integer, Integer> disc,
            Map<Integer, Integer> low,
            Map<Integer, Boolean> onStack,
            Deque<Integer> stack,
            Map<Integer, Integer> sccId,
            List<List<Integer>> sccs
    ) {
        // Each frame: [nodeId, parentId, routeIterator]
        record Frame(int nodeId, Iterator<Route> routes) {}

        Deque<Frame> callStack = new ArrayDeque<>();

        // Initialize start node
        disc.put(startId, timer);
        low.put(startId, timer);
        timer++;
        stack.push(startId);
        onStack.put(startId, true);

        Airport startAirport = airportMap.get(startId);
        Iterator<Route> startIter = startAirport != null
                ? graph.getRoutesFrom(startAirport).iterator()
                : Collections.emptyIterator();

        callStack.push(new Frame(startId, startIter));

        while (!callStack.isEmpty()) {
            Frame frame = callStack.peek();
            int u = frame.nodeId();

            if (frame.routes().hasNext()) {
                Route r = frame.routes().next();
                int v = r.getDestinationAirportID();

                if (!airportMap.containsKey(v)) continue;

                if (!disc.containsKey(v)) {
                    // Tree edge: push new frame for v
                    disc.put(v, timer);
                    low.put(v, timer);
                    timer++;
                    stack.push(v);
                    onStack.put(v, true);

                    Airport vAirport = airportMap.get(v);
                    Iterator<Route> vIter = vAirport != null
                            ? graph.getRoutesFrom(vAirport).iterator()
                            : Collections.emptyIterator();

                    callStack.push(new Frame(v, vIter));

                } else if (onStack.getOrDefault(v, false)) {
                    // Back edge: v is already on stack — same SCC, update low[u]
                    low.put(u, Math.min(low.get(u), disc.get(v)));
                }

            } else {
                // All neighbors of u processed — pop frame
                callStack.pop();

                // Propagate low[u] up to parent
                if (!callStack.isEmpty()) {
                    int parent = callStack.peek().nodeId();
                    low.put(parent, Math.min(low.get(parent), low.get(u)));
                }

                // Check if u is the root of a completed SCC
                if (low.get(u).equals(disc.get(u))) {
                    List<Integer> scc = new ArrayList<>();
                    int sccIndex = sccs.size();
                    while (true) {
                        int w = stack.pop();
                        onStack.put(w, false);
                        sccId.put(w, sccIndex);
                        scc.add(w);
                        if (w == u) break;
                    }
                    sccs.add(scc);
                }
            }
        }
    }
}