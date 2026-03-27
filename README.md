# Flight Connection Optimizer

## Overview
Flight Connection Optimizer is a full-stack project combining **SQL Server**, **Java**, and **React** to analyze airline networks. It computes the cheapest and fastest routes, finds reachable airports within limited connections, and identifies critical hubs using graph algorithms such as **Dijkstra**, **BFS**, and **articulation point detection**. The system supports real-world-inspired datasets and database-backed route optimization.

---

## Core Features

### Required
- **Graph representation** using adjacency lists
- **Shortest path algorithms**:
  - Dijkstra for cheapest route
  - Dijkstra for fastest route
- **Reachability**: BFS to find all airports reachable within ≤K connections
- **Critical airports detection**: find articulation points
- **Edge case handling**:
  - No route exists
  - Source equals destination
  - Airport not in dataset
- **Complexity analysis** for all graph operations
- Minimum Spanning Tree using Prim’s or Kruskal’s algorithm
- Travel budget mode: reachable destinations within a cost limit

---

## Technology Stack
- **Backend:** Java  
- **Database:** SQL Server  
- **Frontend:** React + JavaScript 

---

## Usage

### Database Setup
1. Create database in **SQL Server**  
2. Import database  
3. Ensure tables exist: `Airports`, `Flights`, `Routes` and others

### Backend
```bash
# Compile Java backend
javac -d bin src/*.java

# Run backend
java -cp bin Main
```
### Frontend
```# Install dependencies
npm install

# Start React app
npm start
```

## Authors
- **Artyom Mkrtchyan**  
- **Mane Mazmandyan**   
- **Davit Arakelyan**

## Notes
Combines Database (SQL Server) + Data Structures & Algorithms (Java) + Frontend (React)
Designed for scalability, allowing addition of more airports and routes easily
Demo interface supports exploration of routes, travel costs, and network connectivity
