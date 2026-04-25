# ✈️ Flight Connection Optimizer

> **Academic Project** — Developed as part of two courses: **Data Structures** & **Databases** at UFAR, 2026

🔗 **[Live Demo](https://flightms.vercel.app/)**

---

## 📌 Overview

Every airline booking engine needs to answer questions like: what is the cheapest way to get from city A to city B? What airports are reachable from a given hub? Which routes are structurally most critical?

This project builds a **flight network analyzer** using real-world-inspired data and applies multiple graph algorithms to answer these questions — all backed by a real SQL Server database and served through a live web interface.

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

---

## 🗂️ Data

- **50+ airports** worldwide
- **200+ routes** with cost and distance as edge weights
- Network represented as a **weighted directed graph** using adjacency lists
- All data stored in **Microsoft SQL Server** (Azure)

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
**Artyom Mkrtchyan** **Mane Mazmandyan**

---

*Université Française en Arménie (UFAR) — 2026*  
*Courses: Data Structures & Databases*
