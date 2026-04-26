# ✈️ Flights Management System

> **Academic Project** — Developed as part of two courses: **Data Structures** & **Databases** at UFAR, 2026

🔗 **[Live Demo](https://flightms.vercel.app/)**

---

## 📌 Overview

  The Flights Management System is a web-based project that models airports and routes as a weighted directed graph, helping users easily explore and analyze flight connections. 
  
  It helps answer practical questions faced by any airline, such as: what is the cheapest way to travel from city A to city B? Which route is the fastest? What airports can be reached with a limited number of layovers or within a fixed budget? And which airports are so important that their removal would disrupt the entire network? 
  
  All of this is powered by efficient graph algorithms, a Microsoft SQL Server database, and a modern React frontend, providing clear and interactive results.

---

## 🎯 Objectives
- Provide users with the cheapest and fastest flight routes between any two airports
- Analyze network connectivity and find all airports reachable within a limited number of layovers
- Detect critical airports (articulation points) whose removal would significantly disrupt the flight network
- Enable budget-based exploration — show all destinations reachable within a given cost limit
- Seamlessly integrate graph algorithms with a real relational database (SQL Server)
- Deliver an intuitive and interactive web interface with map visualization of routes and results

---

## 🧠 Algorithms Implemented

| Algorithm | Purpose | Complexity |
|---|---|---|
| **Dijkstra** (cheapest) | Find the lowest-cost route between two airports | O((V + E) log V) |
| **Dijkstra** (fastest) | Find the shortest-distance route between two airports | O((V + E) log V) |
| **BFS (K-connections)** | Find all airports reachable within K layovers | O(V + E) |
| **Articulation Points** | Detect critical airports whose removal disconnects the network | O(V + E) |
| **Prim's MST** *(bonus)* | Compute the Minimum Spanning Tree of the flight network | O(E log V) |
| **Budget Mode** *(bonus)* | Find all destinations reachable within a given cost budget | O((V + E) log V) |

## 🔗 Main Data Structures Used

- **Graph Adjacency List** (for efficient neighbor traversal)
- **Priority Queue** (for Dijkstra’s algorithm)
- **Queue** (for BFS)
- **Disjoint Set / DFS-based structures** (for Articulation Points and Prim’s MST)
---

## 🗂️ Data

- **50+ airports** worldwide
- **200+ routes** with cost and distance as edge weights
- Network represented as a **weighted directed graph** using adjacency lists
- All data stored in **Microsoft SQL Server** (Azure)

---

## 🧩 Approach
- Model the flight network as a **weighted directed graph**
- Store all airports and routes persistently in **Microsoft SQL Server**
- Implement all graph algorithms and business logic in the **Java backend**
- Expose results through a clean **REST API**
- Load graph data from the database into memory for efficient algorithm execution
- Visualize routes, paths, and analysis results on an interactive map in the **React frontend**

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | React, TypeScript, Vite, Tailwind CSS, Leaflet.js |
| **Backend** | Java 17, `com.sun.net.httpserver` (plain HTTP server) |
| **Database** | Microsoft SQL Server (Azure SQL Database) |
| **Build** | Maven (backend), npm/Vite (frontend) |
| **Deployment** | Railway (backend), Vercel (frontend), Azure SQL (database) |

---

## 🚀 Deployment

The project is fully deployed and accessible online:

- **Frontend** → [Vercel](https://vercel.com) — auto-deploys from `main` branch
- **Backend** → [Railway](https://railway.app) — containerized via Dockerfile, connects to Azure
- **Database** → [Azure SQL Database](https://azure.microsoft.com) — cloud-hosted Microsoft SQL Server

Architecture:
```
User → Vercel (React) → Railway (Java API :8081) → Azure SQL Server
```

---

## 💻 Run Locally

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 18+
- Microsoft SQL Server (local or remote)

### 1. Clone the repository
```bash
git clone https://github.com/Artyomkrtchyan/flightManagementSystem.git
cd flightManagementSystem
```

### 2. Set up the database
- Restore the database from `flights.bak` into your local SQL Server instance
- Or run the SQL scripts manually

### 3. Configure the backend connection
Open `backend/src/Main.java` and `backend/src/DatabaseHelper.java` and update the connection string:
```java
String url = "jdbc:sqlserver://localhost:1433;databaseName=Flights;encrypt=true;trustServerCertificate=true";
return DriverManager.getConnection(url, "your_user", "your_password");
```

### 4. Build and run the backend
```bash
cd backend
mvn clean package -DskipTests
java -jar target/backend.jar
# Server starts on http://localhost:8081
```

### 5. Run the frontend
```bash
cd frontend
echo "VITE_API_URL=http://localhost:8081" > .env.local
npm install
npm run dev
# Open http://localhost:5173
```

---

## 🔌 API Endpoints

| Endpoint | Description |
|---|---|
| `GET /graph` | Returns all airports and routes |
| `GET /fastest?id=1&to=2` | Shortest distance path (Dijkstra) |
| `GET /cheapest?id=1&to=2` | Cheapest cost path (Dijkstra) |
| `GET /bfs?id=1&k=3` | Airports reachable within K connections |
| `GET /critical` | Articulation points (critical airports) |
| `GET /mst` | Minimum Spanning Tree routes |
| `GET /budget?id=1&maxBudget=500` | Airports reachable within budget |
| `GET /api/tables` | List all database tables |
| `GET /api/data?table=Airports` | Fetch table data |

---

## 📁 Project Structure

```
flightManagementSystem/
├── backend/
│   ├── src/
│   │   ├── Main.java              # HTTP server & endpoints
│   │   ├── DatabaseHelper.java    # DB operations
│   │   ├── graph/
│   │   │   ├── DijkstraFlexible.java
│   │   │   ├── BFS.java
│   │   │   ├── ArticulationPoints.java
│   │   │   ├── PrimMST.java
│   │   │   ├── BudgetFinder.java
│   │   │   └── FlightGraph.java
│   │   └── model/
│   │       ├── Airport.java
│   │       └── Route.java
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── pages/
│   │   │   ├── Index.tsx          # Main map interface
│   │   │   └── Admin.tsx          # DB admin panel
│   │   └── components/
│   └── package.json
└── README.md
```

---

## 👥 Authors
- **Artyom Mkrtchyan** 
- **Mane Mazmandyan**
- **Davit Arakelyan**

---

## 👥 TEAM CONTRIBUTIONS

All commits were made from Artyom's GitHub account as the team worked together in shared in-person sessions on a single machine.

Artyom Mkrtchyan
- Backend: Graph algorithms (Dijkstra, Articulation Points), HTTP server setup, API endpoints /fastest /cheapest /critical
- Frontend: Map visualization (Leaflet), route highlighting, algorithm result rendering
- Database: Schema design (Airports, Routes, Cities tables), index optimization, deployment (Railway, Azure, Vercel)

Mane Mazmandyan
- Backend: BFS & Budget Finder algorithms, API endpoints /bfs /budget /mst
- Frontend: Navigation bar, algorithm selector UI, status bar, responsive layout
- Database: Stored procedures (sp_BookFlight, sp_FindFlightsByCities), views (View_FlightBoard, View_RouteProfit)

Davit Arakelyan
- Backend: Prim's MST algorithm, DatabaseHelper CRUD operations, API endpoints /api/tables /api/data /api/add /api/delete
- Frontend: Admin panel (DB management UI), add/delete rows, search & filter, bulk delete
- Database: Data population (30+ airports, 80+ routes), triggers (trg_AfterTicketUpdate), query optimization

---

*Université Française en Arménie (UFAR) — 2026*  
*Courses: Data Structures & Databases 1*
