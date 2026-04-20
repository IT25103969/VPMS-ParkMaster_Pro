# VPMS-ParkMaster_Pro

A robust Vehicle Parking Management System (VPMS) designed with clean architecture, object-oriented principles, and reliable file-based data persistence.

## Key Architecture & Design
- **OOP Excellence:** The system utilizes **Inheritance** and **Polymorphism** (via an abstract `User` base class for `Staff` and `Member` entities) to ensure scalable, maintainable, and reusable code.
- **Data Persistence:** Uses a secure, file-based JSON persistence layer managed via **Java I/O**, ensuring data integrity without the overhead of an external database.
- **Layered Architecture:** Follows a modular Spring Boot structure:
    - **Models:** Domain entities with inheritance.
    - **Services:** Business logic and calculation.
    - **Repositories:** Abstracted file-system operations (JSON serialization/deserialization).
    - **Controllers:** RESTful API endpoints.

## Technology Stack
- **Backend:** Java 17+, Spring Boot
- **Persistence:** Jackson JSON library + Java NIO (File-based)
- **Frontend:** Vanilla JavaScript, HTML5, CSS3

## Features
- **User Management:** Polymorphic management of `Staff` and `Member` accounts.
- **Parking Operations:** Real-time slot status tracking (Occupied/Free).
- **CRUD Operations:** Comprehensive Create, Read, Update, and Delete functionality for all entities (Tickets, Slots, Users, Reports).
- **Logging:** Automatic login tracking and persistence via I/O streams.
- **Fee Management:** Dynamic configuration for membership and parking fees.

## Project Structure
```
VPMS-ParkMaster_Pro/
├── data/                # JSON-based data storage (I/O)
├── src/main/java/com/example/parking/
│   ├── controller/      # API Layer
│   ├── model/           # Entities (User, Staff, Member, etc.)
│   ├── repository/      # Java I/O Persistence Layer
│   └── service/         # Business logic
└── pom.xml
```

## How to Run
1. **Navigate to project folder**: `cd VPMS-ParkMaster_Pro`
2. **Build/Run**: `mvn spring-boot:run`
3. **Access**: Open [http://localhost:8080](http://localhost:8080) in your browser.
