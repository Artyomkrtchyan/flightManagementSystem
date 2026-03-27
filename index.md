> **FRENCH UNIVERSITY IN ARMENIA**
>
> **Faculty of Computer science and Applied mathematics**
>
> **Subject Databases 1**
>
> **Students Artyom MKRTCHYAN \| Mane MAZMANDYAN \| Davit ARAKELYAN**
>
> **Teacher Varazdat AVETISYAN**
>
> **March 2026**

**Flight Monitoring System**

# Contents {#contents .TOC-Heading}

[Team Contribution [4](#team-contribution)](#team-contribution)

[Problem Description and Requirements Analysis
[5](#problem-description-and-requirements-analysis)](#problem-description-and-requirements-analysis)

[Conceptual Design Explanation
[6](#conceptual-design-explanation)](#conceptual-design-explanation)

[• Main Data Objects [6](#main-data-objects)](#main-data-objects)

[• Main Business Rules [6](#main-business-rules)](#main-business-rules)

[• System Data Requirements
[6](#system-data-requirements)](#system-data-requirements)

[• System Processes [6](#system-processes)](#system-processes)

[• Flow Charts [7](#flow-charts)](#flow-charts)

[ER Diagram and Logical Schema description
[8](#_Toc225530033)](#_Toc225530033)

[• ER Model Transformation [10](#_Toc225530034)](#_Toc225530034)

[• Relational Schema [10](#relational-schema)](#relational-schema)

[• Relationships and Cardinalities [11](#_Toc225530036)](#_Toc225530036)

[• Constraints [11](#constraints)](#constraints)

[• Design Decisions [11](#design-decisions)](#design-decisions)

[Normalization Process Documentation
[12](#normalization-process-documentation)](#normalization-process-documentation)

[• First Normal Form (1NF)
[12](#first-normal-form-1nf)](#first-normal-form-1nf)

[• Second Normal Form (2NF)
[12](#second-normal-form-2nf)](#second-normal-form-2nf)

[• Third Normal Form (3NF)
[12](#third-normal-form-3nf)](#third-normal-form-3nf)

[Relational Algebra Query Demonstrations
[13](#relational-algebra-query-demonstrations)](#relational-algebra-query-demonstrations)

[SQL Scripts [15](#_Toc225529325)](#_Toc225529325)

[Documentation of Views, Indexes, Triggers, and Stored Procedures
[16](#documentation-of-views-indexes-triggers-and-stored-procedures)](#documentation-of-views-indexes-triggers-and-stored-procedures)

[Database Deployment Description
[18](#database-deployment-description)](#database-deployment-description)

[Project Conclusions and Results
[19](#project-conclusions-and-results)](#project-conclusions-and-results)

# **Team Contribution**

# 

The Flight Management System project involved collaborative efforts
across multiple areas of database design, implementation, and
integration. This table highlights the areas of responsibility, allowing
each team member to be credited for their contributions in designing,
implementing, and optimizing the database system, as well as integrating
it with the user interface and documenting the project. The team
contributions are summarized below:

  ---------------------------------------------------------------
  **Project Area**                                **Team
                                                  Member(s)**
  ----------------------------------------------- ---------------
  Project Planning & Requirements Analysis        Worked Together

  Conceptual Design (ER Diagram, Entities &       Mane Mazmandyan
  Relationships)                                  

  Logical Design (Relational Schema,              David Arakelyan
  Normalization)                                  

  Physical Database Implementation                Mane Mazmandyan

  Data Population & DML                           David Arakelyan

  Views, Indexes, Triggers, Stored Procedures     Artyom
                                                  Mkrtchyan

  Database Testing & Query Optimization           David Arakelyan

  Frontend/UI Development (React interface, user  Artyom
  interactions)                                   Mkrtchyan

  Final Report & Presentation                     Worked Together

                                                  

                                                  
  ---------------------------------------------------------------

# **Problem Description and Requirements Analysis**

The project focuses on the design and implementation of a relational
database system for managing airline operations. The system is intended
to store, organize, and process data related to airlines, aircraft,
airports, routes, flights, passengers, and ticketing.

The database includes the following core tables: Aircraft, Airlines,
Airports, Cities, Countries, Flights, Passengers, Routes, Tickets, and
TicketAuditLog. These entities represent the main components of an
airline management system and are interconnected through well-defined
relationships.

The primary problem addressed by this project is ensuring data
consistency and integrity across multiple related entities. For example,
each aircraft must belong to a specific airline, each route must connect
valid airports, and each flight must correctly reference both a route
and an aircraft.

The database is implemented using SQL and supports full CRUD operations,
analytical queries, and automated processes. It is designed to be
extensible and can be integrated with external applications if needed.

Overall, the system provides a structured and efficient solution for
managing complex airline-related data while maintaining consistency,
traceability, and performance.

# **Conceptual Design Explanation**

The conceptual design of the system is based on the analysis of the
airline management domain and its core operations. The system models the
real-world entities involved in airline operations and defines the
relationships between them.

### Main Data Objects

- **Airlines** -- represents airline companies

- **Aircraft** -- represents airplanes belonging to airlines

- **Airports** -- represents departure and arrival locations

- **Cities** and **Countries** -- represent geographical hierarchy

- **Routes** -- define connections between airports

- **Flights** -- represent scheduled flights

- **Passengers** -- store passenger information

- **Tickets** -- represent bookings made by passengers

### Main Business Rules

- Each aircraft must be assigned to exactly one airline

- Each flight must be linked to a valid route and aircraft

- Each route must connect two existing airports

- A ticket must be associated with a valid passenger and flight

### System Data Requirements

- Entity identifiers (primary keys)

- Relationships (foreign keys)

- Operational data (dates, locations, statuses)

- Passenger and booking details

### System Processes

- Adding and managing airlines and aircraft

- Creating and managing routes between airports

- Scheduling flights based on routes and aircraft

- Booking tickets for passengers

- Retrieving data using queries and views

### Flow Charts

![](media/image2.png){width="5.883333333333334in"
height="6.0777777777777775in"}

\\

Figure 1: Sequence Diagram

![Figure 2: Use-Action Diagram](media/image4.svg){width="6.2in"
height="7.062341426071741in"}

![](media/image5.jpeg){width="6.120833333333334in"
height="4.132454068241469in"}

Figure 3: Activity Diagram

### Sequence, Use Case, and Activity Diagrams

The sequence diagrams illustrate the step-by-step interactions between
system components during key processes such as flight booking, ticket
cancellation, and flight information retrieval. They demonstrate the
flow of messages between actors (e.g., passengers, system) and objects
(e.g., Flights, Tickets, Database) to ensure correct execution of each
operation.

The use case diagrams provide a high-level overview of the system's
functionalities, showing how different users interact with the system.
Each use case represents a specific goal, such as booking a ticket or
searching for flights, and identifies the actors involved and their
responsibilities.

The activity diagrams capture the workflow of major processes,
highlighting decision points, parallel actions, and the sequence of
tasks. These diagrams help visualize the system's dynamic behavior and
verify that all processes are logically consistent and complete.

Together, these diagrams document the system's functional and behavioral
aspects, supporting both design validation and future maintenance.

# **ER Diagram and Logical Schema description**

# 

![Figure 4 ER Diagram](media/image6.png){width="7.043478783902012in"
height="4.0055555555555555in"}

[]{#_Toc225530034 .anchor}

- ER Model Transformation

The Entity--Relationship model was transformed into a relational schema.
Each entity in the ER diagram is represented as a table, and
relationships are implemented using foreign keys to maintain referential
integrity.

### Relational Schema

- Airlines(**AirlineID** PK, Name, CountryID FK)

- Aircraft(**AircraftID** PK, Model, Capacity, AirlineID FK)

- Countries(**CountryID** PK, CountryName)

- Cities(**CityID** PK, CityName, CountryID FK)

- Airports(**AirportID** PK, Code, AirportName, CityID FK, Latitude,
  Longitude)

- Routes(**RouteID** PK, SourceAirportID FK, DestinationAirportID FK,
  BaseDistanceKM, Cost)

- Flights(**FlightID** PK, RouteID FK, AirlineID FK, AircraftID FK,
  DepartureTime, ArrivalTime, DurationMinutes, Code)

- Passengers(**PassengerID** PK, FirstName, LastName, PassportNumber)

- Tickets(**TicketID** PK, PassengerID FK, FlightID FK, SeatNumber,
  BookingDate, TicketStatus, Class)

[]{#_Toc225530036 .anchor}

### Relationships and Cardinalities

- One airline can own many aircraft (1:N)

- One airline can operate many flights (1:N)

- One country can contain many cities (1:N)

- One city can contain many airports (1:N)

- One airport can be used in many routes (1:N)

- One route connects two airports source and destination (1:N)

- One route can have many flights (1:N)

- One aircraft can be assigned to many flights (1:N)

- One passenger can have many tickets (1:N)

- One flight can have many tickets (1:N)

- One country can have many airlines (1:N)

### Constraints

- Primary keys uniquely identify each record and cannot be NULL

- Foreign keys ensure referential integrity between related tables

- Each aircraft must belong to an existing airline

- Each flight must reference a valid route, airline, and aircraft

- Each route must reference valid source and destination airports

- Each ticket must reference a valid passenger and flight

- PassportNumber should be unique for each passenger

### Design Decisions

- The separation of Countries, Cities, and Airports ensures
  normalization and avoids redundancy

- Routes are modeled as a separate entity to represent connections
  between airports

- Flights are linked to both routes and aircraft to ensure consistency
  in scheduling

- Tickets act as a bridge between passengers and flights

- Additional attributes such as DurationMinutes and Cost are included
  for analytical queries

![Figure 5: Relationships
Diagram](media/image7.png){width="6.982638888888889in"
height="3.1215277777777777in"}

# **Normalization Process Documentation**

### First Normal Form (1NF)

The database satisfies the First Normal Form (1NF) as:

- All attributes contain atomic (indivisible) values

- There are no repeating groups or multi-valued attributes

- Each table has a defined primary key

For example, in the **Passengers** table, attributes such as FirstName,
LastName, and PassportNumber store single values, and each record is
uniquely identified by PassengerID.

### Second Normal Form (2NF)

The database satisfies the Second Normal Form (2NF) because:

- It is already in 1NF

- All non-key attributes are fully functionally dependent on the primary
  key

All tables use single-attribute primary keys (e.g., FlightID, TicketID),
so partial dependency does not exist.

For example:

- In the **Flights** table, attributes such as DepartureTime and
  ArrivalTime depend entirely on FlightID

- In the **Tickets** table, SeatNumber and BookingDate depend only on
  TicketID

### Third Normal Form (3NF)

The database satisfies the Third Normal Form (3NF) because:

- It is already in 2NF

- There are no transitive dependencies

Non-key attributes do not depend on other non-key attributes.

For example:

- Country information is stored in the **Countries** table, not in
  Cities or Airports

- City information is stored in the **Cities** table, avoiding
  duplication in Airports

- Airline information is separated from Aircraft

This eliminates redundancy and ensures data consistency.

# **Relational Algebra Query Demonstrations**

At this stage, the work has been carried out purely, demonstrating how
relational algebra operations such as selection, projection, join,
union, intersection, and difference are applied to the database schema.

![](media/image8.png){width="5.43069116360455in"
height="2.3877799650043743in"}

![Figure 7: Intersection](media/image9.png){width="5.430688976377953in"
height="1.9890977690288714in"}

Figure 6: Union

# ![](media/image10.png){width="5.43069116360455in" height="2.835488845144357in"}

#  

![Figure 10: Projection](media/image11.png){width="5.430555555555555in"
height="2.8333333333333335in"}

![Figure 9: Selection](media/image12.png){width="5.430690069991251in"
height="2.86619750656168in"}

Figure 8: Join

[]{#_Toc225529325 .anchor}SQL Scripts

![](media/image13.png){width="5.0680555555555555in"
height="1.7069444444444444in"}

![Figure 11: DDL
Statement](media/image14.png){width="5.0681813210848645in"
height="1.7653160542432196in"}

![Figure 12: DML
Statement](media/image15.png){width="5.113636264216973in"
height="1.8628248031496062in"}

![Figure 14: DCL
Statement](media/image16.png){width="5.107241907261592in"
height="2.445699912510936in"}

Figure 13: DQL Statement

# **Documentation of Views, Indexes, Triggers, and Stored Procedures**

This section presents the implementation of Views, Indexes, Triggers,
and Stored Procedures within the database system as part of the physical
design stage. These components were developed to support efficient data
management and system functionality.

The following screenshots illustrate the creation, configuration, and
execution of these database objects, demonstrating their integration and
correct operation within the system.

Figure 16 Indexes

Figure 15: Views

![](media/image17.png){width="5.202083333333333in"
height="2.4784722222222224in"}![](media/image18.png){width="5.202083333333333in"
height="2.4993055555555554in"}

![](media/image19.png){width="5.202083333333333in"
height="2.482638888888889in"}

![Figure 18: Triggers](media/image20.png){width="5.202083333333333in"
height="2.477777777777778in"}

Figure 17: Stored Procedures

**\**

# **Database Deployment Description**

The Flight Management System database was developed in SQL Server
Management Studio, with TCP/IP enabled and firewall configured for
remote access. It includes tables for Flights, Tickets, Routes, and
Airports with proper keys. Data was populated via SQL INSERTs, and
Views, Indexes, Triggers, and Stored Procedures were implemented for
optimization and automation.

The front-end (JavaScript/React) interacts with the backend (Java
HttpServer), sending HTTP requests that execute SQL queries and return
JSON data. Users can view, add, and delete records. Data processing
algorithms from the Data Structure course were used to ensure efficient
search, sorting, and retrieval of flight information.

![Figure 18: Main Page of
Website](media/image21.png){width="5.8497200349956255in"
height="3.0989588801399823in"}

![Figure 19: DB Page of
Website](media/image22.png){width="5.849721128608924in"
height="3.0223556430446195in"}

# **Project Conclusions and Results** 

The project successfully designed and implemented a relational database
for airline operations, addressing the challenge of maintaining
consistency and integrity across multiple interconnected entities. Core
tables---including Aircraft, Airlines, Airports, Routes, Flights,
Passengers, and Tickets---were structured with primary and foreign key
constraints, ensuring correct relationships and referential integrity.

Data population, indexing, and the implementation of Views, Triggers,
and Stored Procedures enabled efficient query execution, automated
routine processes, and supported full CRUD operations. The database
reliably enforces business rules, tracks ticketing history through audit
logs, and supports analytical queries for operational insights.

Overall, the system provides a robust, consistent, and extensible
solution for managing complex airline data, resolving the primary
problem of ensuring data accuracy and traceability across all related
entities.

GitHub link:

<https://github.com/Artyomkrtchyan/FlightManagmentSystem>
